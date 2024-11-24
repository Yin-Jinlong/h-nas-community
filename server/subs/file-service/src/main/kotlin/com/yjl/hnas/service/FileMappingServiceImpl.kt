package com.yjl.hnas.service

import com.yjl.hnas.entity.IFileMapping
import com.yjl.hnas.mapper.FileMappingMapper
import com.yjl.hnas.preview.PreviewException
import com.yjl.hnas.preview.PreviewGeneratorFactory
import com.yjl.hnas.service.FileMappingService.Companion.previewFile
import com.yjl.hnas.utils.del
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.*
import org.apache.tika.mime.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.util.*

/**
 * @author YJL
 */
@Service
class FileMappingServiceImpl(
    val fileMappingMapper: FileMappingMapper,
    val previewGeneratorFactory: PreviewGeneratorFactory
) : FileMappingService {

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val genPreviewTasks = Vector<String>()

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

    fun IFileMapping.type() = MediaType.parse("$type/$subType")

    override fun getPreviewFile(mapping: IFileMapping): File? = with(mapping) {
        val mediaType = type()
        if (!previewGeneratorFactory.canPreview(mediaType))
            return null
        val cache = previewFile(dataPath)
        if (cache.exists())
            cache
        else null
    }

    @Transactional
    override fun getPreview(mapping: IFileMapping): String? = with(mapping) {
        val mediaType = type()
        if (!previewGeneratorFactory.canPreview(mediaType))
            return null
        val cache = previewFile(dataPath)
        if (cache.exists())
            return dataPath
        else {
            cache.parentFile.apply {
                if (!exists())
                    mkdirs()
            }
        }
        return@with synchronized(genPreviewTasks) {
            if (!genPreviewTasks.contains(hash)) {
                genPreviewTasks += cache.name
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