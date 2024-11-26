package com.yjl.hnas.service.impl

import com.yjl.hnas.entity.Hash
import com.yjl.hnas.entity.IFileMapping
import com.yjl.hnas.mapper.FileMappingMapper
import com.yjl.hnas.option.PreviewOption
import com.yjl.hnas.preview.PreviewException
import com.yjl.hnas.preview.PreviewGeneratorFactory
import com.yjl.hnas.service.FileMappingService
import com.yjl.hnas.service.FileMappingService.Companion.previewFile
import com.yjl.hnas.utils.del
import io.github.yinjinlong.spring.boot.util.getLogger
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.*
import org.apache.tika.mime.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileNotFoundException
import java.util.*

private typealias CacheFileFn = (String) -> File

/**
 * @author YJL
 */
@Service
class FileMappingServiceImpl(
    val fileMappingMapper: FileMappingMapper,
    val previewGeneratorFactory: PreviewGeneratorFactory,
    val previewOption: PreviewOption,
) : FileMappingService {

    private val logger = getLogger()

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val genPreviewTasks = Vector<Hash>()

    override fun getMapping(hash: Hash): IFileMapping? {
        return fileMappingMapper.selectByHash(hash)
    }

    @Transactional
    override fun deleteMapping(hash: Hash) {
        fileMappingMapper.deleteById(hash)
    }

    override fun getSize(hash: Hash): Long {
        val fm = getMapping(hash) ?: throw IllegalArgumentException("hash not found")
        if (fm.size < 0) {
            fm.size = File(fm.dataPath).length()
            fileMappingMapper.updateSize(hash, fm.size)
        }
        return fm.size
    }

    suspend fun genPreview(
        cache: File,
        hash: Hash,
        dataPath: String,
        mediaType: MediaType,
        maxSize: Int,
        quality: Float
    ) {
        val file = FileMappingService.dataFile(dataPath)
        try {
            val data =
                previewGeneratorFactory.getPreview(file.inputStream(), mediaType, maxSize, quality) ?: return let {
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
        } catch (e: FileNotFoundException) {
            logger.warning("File not found :" + e.message)
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
    protected fun genPreview(
        mapping: IFileMapping,
        fn: CacheFileFn,
        maxSize: Int,
        quality: Float
    ): String? = with(mapping) {
        val mediaType = type()
        if (!previewGeneratorFactory.canPreview(mediaType))
            return null
        val cache = fn(dataPath)
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
                genPreviewTasks += hash
                scope.launch {
                    genPreview(cache, hash, dataPath, mediaType, maxSize, quality)
                    synchronized(genPreviewTasks) {
                        genPreviewTasks -= hash
                    }
                }
            }
            ""
        }
    }


    @Transactional
    override fun getThumbnail(mapping: IFileMapping): String? = genPreview(
        mapping,
        FileMappingService::thumbnailFile,
        previewOption.thumbnailSize,
        previewOption.thumbnailQuality
    )

    @Transactional
    override fun getPreview(mapping: IFileMapping): String? = if (mapping.type != "image") null else genPreview(
        mapping, FileMappingService::previewFile,
        previewOption.previewSize,
        previewOption.previewQuality
    )

    @PreDestroy
    fun destroy() {
        scope.coroutineContext.cancelChildren()
    }
}