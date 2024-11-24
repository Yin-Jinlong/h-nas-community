package com.yjl.hnas.service

import com.yjl.hnas.entity.FileMapping
import com.yjl.hnas.entity.IFileMapping
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.error.ErrorCode
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
import java.io.InputStream
import java.io.RandomAccessFile
import java.util.*
import kotlin.io.path.name

/**
 * @author YJL
 */
@Service
class FileMappingServiceImpl(
    val fileMappingMapper: FileMappingMapper,
    val previewGeneratorFactory: PreviewGeneratorFactory,
    val virtualFileService: VirtualFileService
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

    fun previewFile(dataPath: String): File = File(FileMappingService.PreviewDir, "$dataPath.jpg")

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
            return "$dataPath.jpg"
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

    fun tmpFile(hash: String) = FileMappingService.dataFile("tmp/$hash.tmp")

    /*    override fun uploadRange(
            uid: Uid,
            path: PubPath,
            hash: String,
            off: Long,
            fileSize: Long,
            ins: InputStream,
            dataHash: String
        ) {
            val tmp = tmpFile(hash).apply {
                parentFile.apply {
                    if (!exists())
                        mkdirs()
                }
            }
            val rf = RandomAccessFile(tmp, "rw")
            rf.seek(off)
            val inBuf = ByteArray(1024 * 1024)
            var len = 0
            kotlin.runCatching {
                while (true) {
                    val l = ins.read(inBuf)
                    if (l == -1)
                        break
                    len += l
                    rf.write(inBuf, 0, l)
                }
            }
            if (rf.filePointer > fileSize)
                rf.setLength(fileSize)
            rf.close()
        }*/
    /*
        override fun uploadEnd(uid: Uid, hash: String, path: PubPath, type: MediaType?, fileSize: Long) {
            run {
                if (type == null)
                    return@run
                val file = FileMappingService.dataFile("$type/$hash")
                if (file.exists()) {
                    virtualFileService.createPubFile(
                        uid,
                        path,
                        type,
                        fileSize,
                        hash = hash
                    )
                    return
                }
            }

            val tmp = tmpFile(hash)
            val fileType = tmp.inputStream().buffered().use { FileDetector.detect(it, path.name) }
            if (type != null && fileType != type)
                throw ErrorCode.BAD_FILE_FORMAT.data("bad file format")
            val file = FileMappingService.dataFile("$fileType/$hash")
            if (!file.exists()) {
                val fileHash = tmp.sha256.base64Url
                if (fileHash != hash)
                    throw ErrorCode.BAD_REQUEST.data("bad hash")
                tmp.renameTo(file)
            }
            virtualFileService.createPubFile(
                uid,
                path,
                fileType,
                fileSize,
                hash = hash
            )
            if (getMapping(hash) == null) {
                fileMappingMapper.insert(
                    FileMapping(
                        hash = hash,
                        dataPath = "$type/$hash",
                        type = fileType.type,
                        subType = fileType.subtype,
                        preview = previewGeneratorFactory.canPreview(fileType),
                        size = fileSize
                    )
                )
            }
        }*/

    @PreDestroy
    fun destroy() {
        scope.coroutineContext.cancelChildren()
    }
}