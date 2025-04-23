package io.github.yinjinlong.hnas.service.impl

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.yinjinlong.hnas.data.ChapterInfo
import io.github.yinjinlong.hnas.data.DataHelper
import io.github.yinjinlong.hnas.data.HLSStream
import io.github.yinjinlong.hnas.data.HLSStreamInfo
import io.github.yinjinlong.hnas.entity.Hash
import io.github.yinjinlong.hnas.entity.IFileMapping
import io.github.yinjinlong.hnas.entity.VirtualFile
import io.github.yinjinlong.hnas.error.ErrorCode
import io.github.yinjinlong.hnas.fs.VirtualPath
import io.github.yinjinlong.hnas.hls.HLSGenerator
import io.github.yinjinlong.hnas.hls.VideoChapterHelper
import io.github.yinjinlong.hnas.mapper.FileMappingMapper
import io.github.yinjinlong.hnas.option.PreviewOption
import io.github.yinjinlong.hnas.preview.PreviewException
import io.github.yinjinlong.hnas.preview.PreviewGeneratorHelper
import io.github.yinjinlong.hnas.service.FileMappingService
import io.github.yinjinlong.hnas.service.VirtualFileService
import io.github.yinjinlong.hnas.task.BackgroundTasks
import io.github.yinjinlong.hnas.utils.del
import io.github.yinjinlong.hnas.utils.isVideoMediaType
import io.github.yinjinlong.hnas.utils.mkParent
import io.github.yinjinlong.spring.boot.util.getLogger
import org.apache.tika.mime.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.DefaultTransactionDefinition
import java.io.File
import java.io.FileNotFoundException
import java.net.URLEncoder

private typealias CacheFileFn = (String) -> File

/**
 * @author YJL
 */
@Service
class FileMappingServiceImpl(
    val fileMappingMapper: FileMappingMapper,
    val previewGenerators: PreviewGeneratorHelper,
    val previewOption: PreviewOption,
    val transactionManager: PlatformTransactionManager,
    val virtualFileService: VirtualFileService,
    val gson: Gson,
) : FileMappingService {

    private val logger = getLogger()

    private val previewTypeSet = setOf("image", "video")

    val transactionDefinition = DefaultTransactionDefinition().apply {
        propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
    }

    override fun getMapping(hash: Hash): IFileMapping? {
        return fileMappingMapper.selectByHash(hash)
    }

    @Transactional
    override fun deleteMapping(hash: Hash) {
        fileMappingMapper.deleteById(hash)
    }

    @Transactional
    override fun getSize(hash: Hash): Long {
        val fm = fileMappingMapper.selectByHashLock(hash) ?: throw IllegalArgumentException("hash not found")
        if (fm.size < 0) {
            fm.size = File(fm.dataPath).length()
            fileMappingMapper.updateSize(hash, fm.size)
        }
        return fm.size
    }

    override fun getMediaType(hash: Hash): String {
        return fileMappingMapper.selectMediaTypeByHash(hash)
            ?: throw IllegalStateException("no mapping: $hash")
    }

    private fun updatePreview(hash: Hash, preview: Boolean) {
        val ts = transactionManager.getTransaction(transactionDefinition)
        try {
            val fm = fileMappingMapper.selectByHashLock(hash)
                ?: throw IllegalStateException("FileMapping not found: $hash")
            fileMappingMapper.updatePreview(fm.hash, preview)
            transactionManager.commit(ts)
        } catch (e: Exception) {
            transactionManager.rollback(ts)
            throw e
        }
    }

    suspend fun genPreview(
        cache: File,
        hash: Hash,
        dataPath: String,
        mediaType: MediaType,
        maxSize: Int,
        quality: Float
    ) {
        val file = DataHelper.dataFile(dataPath)
        try {
            val data =
                previewGenerators.getPreview(file, mediaType, maxSize, quality) ?: return
            cache.parentFile.apply {
                if (!exists())
                    mkdirs()
            }
            kotlin.runCatching {
                cache.writeBytes(data)
            }.onSuccess {
                updatePreview(hash, true)
            }.onFailure {
                cache.del()
                throw it
            }
        } catch (e: PreviewException) {
            updatePreview(hash, false)
        } catch (e: FileNotFoundException) {
            logger.warning("File not found :" + e.message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun IFileMapping.type() = MediaType.parse("$type/$subType")

    override fun getPreviewFile(mapping: IFileMapping): File? = with(mapping) {
        val mediaType = type()
        if (!previewGenerators.canPreview(mediaType))
            return null
        val cache = DataHelper.previewFile(dataPath)
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
        if (!previewGenerators.canPreview(mediaType))
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

        BackgroundTasks.run(hash) {
            genPreview(cache, hash, dataPath, mediaType, maxSize, quality)
        }
        return@with ""
    }


    @Transactional
    override fun getThumbnail(mapping: IFileMapping): String? = genPreview(
        mapping,
        DataHelper::thumbnailFile,
        previewOption.thumbnailSize,
        previewOption.thumbnailQuality
    )

    @Transactional
    override fun getPreview(mapping: IFileMapping): String? = if (mapping.type in previewTypeSet) genPreview(
        mapping, DataHelper::previewFile,
        previewOption.previewSize,
        previewOption.previewQuality
    ) else null

    override fun getVideoLiveStream(path: VirtualPath): HLSStreamInfo? {
        val vf = virtualFileService.get(path) as VirtualFile?
            ?: throw ErrorCode.NO_SUCH_FILE.error
        if (vf.hash == null)
            return null
        val fm = getMapping(vf.hash!!)
            ?: throw IllegalStateException("no mapping: ${vf.hash}")
        if (fm.type != "video")
            throw ErrorCode.BAD_FILE_FORMAT.data(vf.name)
        val hash = fm.hash.pathSafe
        val index = DataHelper.hlsIndexFile(hash)
        val prefix = URLEncoder.encode(path.path, "UTF-8")
        if (index.exists()) {
            return HLSStreamInfo("", index.useLines {
                it.mapNotNull { line ->
                    if (line.isBlank()) null
                    else HLSStream(line.toInt(), "$prefix/$line/index.m3u8")
                }.toList()
            })
        }
        val t = BackgroundTasks.run(hash, extra = 0) { task ->
            val videoFile = DataHelper.dataFile(fm.dataPath)
            HLSGenerator.generate(videoFile, 5.0, hash) {
                task.extra = it
            }
        }
        return HLSStreamInfo("~${t.extra}", listOf())
    }

    override fun getVideoChapters(path: VirtualPath): List<ChapterInfo> {
        val vf = virtualFileService.get(path) ?: throw ErrorCode.NO_SUCH_FILE.error
        val fm = getMapping(vf.hash ?: throw ErrorCode.BAD_REQUEST.error)
            ?: throw IllegalStateException("no mapping: ${vf.hash}")
        if (!fm.type.isVideoMediaType)
            throw ErrorCode.BAD_FILE_FORMAT.data(vf.name)
        val chapterFile = DataHelper.hlsSubFile(vf.hash!!.pathSafe, "chapter")
        return if (!chapterFile.exists()) {
            val fm = getMapping(vf.hash!!)
                ?: throw IllegalStateException("no mapping: ${vf.hash}")
            val chapters = VideoChapterHelper.getChapter(DataHelper.dataFile(fm.dataPath))
            chapters.map {
                ChapterInfo(it.start_time, it.tags?.title ?: "")
            }.also {
                chapterFile.mkParent()
                chapterFile.writeText(gson.toJson(it))
            }
        } else {
            gson.fromJson(
                chapterFile.readText(),
                TypeToken.getParameterized(List::class.java, ChapterInfo::class.java)
            ) as List<ChapterInfo>
        }
    }
}