package com.yjl.hnas.service

import com.yjl.hnas.entity.FileMapping
import com.yjl.hnas.entity.IFileMapping
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.mapper.FileMappingMapper
import com.yjl.hnas.preview.PreviewException
import com.yjl.hnas.preview.PreviewGeneratorFactory
import com.yjl.hnas.tika.FileDetector
import com.yjl.hnas.utils.base64Url
import com.yjl.hnas.utils.del
import io.github.yinjinlong.md.sha256
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.*
import org.apache.tika.mime.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.util.Vector
import kotlin.io.path.name

/**
 * @author YJL
 */
@Service
class FileMappingServiceImpl(
    val fileMappingMapper: FileMappingMapper,
    val previewGeneratorFactory: PreviewGeneratorFactory,
) : FileMappingService {

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val genPreviewTasks = Vector<String>()

    @Transactional
    override fun addMapping(path: PubPath, size: Long, hash: String) {
        val vp = path.toVirtual()
        val file = vp.toFile()
        val fileHash = file.sha256.base64Url
        if (fileHash != hash)
            throw ErrorCode.BAD_HEADER.data("bad hash")
        val type: MediaType
        file.inputStream().buffered().use {
            type = FileDetector.detect(it, path.name)
        }
        fileMappingMapper.insert(
            FileMapping(
                hash = fileHash,
                dataPath = vp.path,
                type = type.type,
                subType = type.subtype,
                preview = previewGeneratorFactory.canPreview(type),
                size = file.length().also {
                    if (it != size)
                        throw IllegalStateException("size not match : $it != $size")
                }
            )
        )
    }

    override fun getMapping(hash: String): IFileMapping? {
        return fileMappingMapper.selectByHash(hash)
    }

    @Transactional
    override fun deleteMapping(hash: String) {
        fileMappingMapper.deleteById(hash)
    }

    override fun getSize(hash: String): Long {
        val fm = getMapping(hash) ?: throw IllegalArgumentException("hash not found")
        if (fm.size < 0) {
            fm.size = File(fm.dataPath).length()
            fileMappingMapper.updateSize(hash, fm.size)
        }
        return fm.size
    }

    suspend fun genPreview(cache: File, hash: String, dataPath: String, mediaType: MediaType) {
        val file = File("data", dataPath)
        try {
            val data = previewGeneratorFactory.getPreview(file.inputStream(), mediaType) ?: return let {
                fileMappingMapper.updatePreview(hash, true)
            }
            cache.parentFile.apply {
                if (!exists())
                    mkdirs()
            }
            kotlin.runCatching {
                cache.writeBytes(data)
            }.onSuccess {
                fileMappingMapper.updatePreview(hash, true)
            }.onFailure {
                cache.del()
            }
        } catch (e: PreviewException) {
            fileMappingMapper.updatePreview(hash, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Transactional
    override fun getPreview(mapping: IFileMapping): String? = with(mapping) {
        val mediaType = MediaType.parse("$type/$subType")
        if (!previewGeneratorFactory.canPreview(mediaType))
            return null
        val name = "$dataPath.jpg"
        val cache = File(FileMappingService.PreviewDir, name)
        if (cache.exists())
            return name
        return@with synchronized(genPreviewTasks) {
            if (!genPreviewTasks.contains(hash)) {
                genPreviewTasks += name
                scope.launch {
                    genPreview(cache, hash, dataPath, mediaType)
                    synchronized(genPreviewTasks) {
                        genPreviewTasks -= hash
                    }
                }
            }
            ""
        }
    }

    @PreDestroy
    fun destroy() {
        scope.coroutineContext.cancelChildren()
    }
}