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
import io.github.yinjinlong.md.sha256
import org.apache.tika.mime.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import kotlin.io.path.name

/**
 * @author YJL
 */
@Service
class FileMappingServiceImpl(
    val fileMappingMapper: FileMappingMapper,
    val previewGeneratorFactory: PreviewGeneratorFactory,
) : FileMappingService {

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

    @Transactional
    override fun getPreview(hash: String, dataPath: String, mediaType: MediaType): String? {
        if (!previewGeneratorFactory.canPreview(mediaType))
            return null
        val name = "$dataPath.jpg"
        val cache = File(FileMappingService.PreviewDir, name)
        if (cache.exists())
            return name
        val file = File("data", dataPath)
        return try {
            val data = previewGeneratorFactory.getPreview(file.inputStream(), mediaType) ?: return let {
                fileMappingMapper.updatePreview(hash, true)
                null
            }
            cache.parentFile.apply {
                if (!exists())
                    mkdirs()
            }
            cache.writeBytes(data)
            fileMappingMapper.updatePreview(hash, true)
            name
        } catch (e: PreviewException) {
            fileMappingMapper.updatePreview(hash, false)
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}