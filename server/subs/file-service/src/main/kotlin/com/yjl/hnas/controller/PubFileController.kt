package com.yjl.hnas.controller

import com.yjl.hnas.annotation.ShouldLogin
import com.yjl.hnas.annotation.TokenLevel
import com.yjl.hnas.data.*
import com.yjl.hnas.entity.Hash
import com.yjl.hnas.entity.VirtualFile
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.service.FileMappingService
import com.yjl.hnas.service.VirtualFileService
import com.yjl.hnas.token.TokenType
import com.yjl.hnas.utils.*
import io.github.yinjinlong.spring.boot.annotations.ResponseEmpty
import io.github.yinjinlong.spring.boot.util.getLogger
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRange
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.file.Files
import kotlin.io.path.name

/**
 * @author YJL
 */
@Controller
@RequestMapping("/api/file/public")
class PubFileController(
    virtualFileSystemProvider: VirtualFileSystemProvider,
    val fileMappingService: FileMappingService,
    val virtualFileService: VirtualFileService
) : WithFS(virtualFileSystemProvider) {

    val logger = getLogger()

    @GetMapping("preview/info")
    fun getFilePreview(path: String): FilePreview {
        val pp = getPubPath(path)
        val vf = virtualFileService.get(pp)
            ?: throw ErrorCode.NO_SUCH_FILE.data(path)
        if (vf.hash != null) {
            val fm = fileMappingService.getMapping(vf.hash!!)
                ?: throw ErrorCode.NO_SUCH_FILE.data(path)
            return FilePreview(
                thumbnail = if (fm.preview)
                    fileMappingService.getThumbnail(fm)
                else null,
                preview = if (fm.preview)
                    fileMappingService.getPreview(fm)
                else null,
            )
        } else {
            return FilePreview()
        }
    }

    @GetMapping("folder/count")
    fun folderChildrenCount(path: String): FolderChildrenCount = withCatch {
        val pp = getPubPath(path).toAbsolutePath()
        val cc = virtualFileService.getFolderChildrenCount(pp)
        FolderChildrenCount(
            pp.path,
            cc.subCount,
            cc.subsCount
        )
    }

    @GetMapping("files")
    fun getFiles(
        @NotBlank(message = "path 不能为空") path: String,
        type: String?
    ): List<FileInfo> = withCatch {
        if (path.isBlank())
            throw ErrorCode.BAD_ARGUMENTS.error
        val p = path.trim().ifEmpty { "/" }

        val pp = getPubPath(p).toAbsolutePath()
        val files = virtualFileService.getByParent(pp, type)

        files.map {
            it.toFileInfo(pp, fileMappingService)
        }.sorted()
    }

    @PostMapping("folder")
    @TokenLevel(TokenType.FULL_ACCESS)
    fun createFolder(
        @RequestParam("path") path: String,
        @ShouldLogin user: UserToken,
    ): Unit = withCatch {
        val p = getPubPath(path)
        Files.createDirectory(p, FileOwnerAttribute(user.data.uid))
    }

    @Async
    @PostMapping("upload")
    @TokenLevel(TokenType.FULL_ACCESS)
    fun uploadFile(
        @ShouldLogin token: UserToken,
        @RequestHeader("Content-ID") pathBase64: String,
        @RequestHeader("Hash") sha256Base64: String,
        @RequestHeader("Content-Range") range: String,
        rawIn: ServletInputStream
    ): Boolean = withCatch {
        val path = getPubPath(pathBase64.unBase64Url)
        val hash = sha256Base64.reBase64Url

        val mr = RangeRegex.matchEntire(range)
            ?: throw ErrorCode.BAD_ARGUMENTS.data(range)

        val start = mr.groupValues[1].toLong()
        val end = mr.groupValues[2].toLong()
        val size = mr.groupValues[3].toLong()

        if (end < start)
            throw ErrorCode.BAD_ARGUMENTS.data(range)

        virtualFileService.upload(
            token.data.uid,
            path.toAbsolutePath(),
            Hash(hash),
            size,
            FileRange(start, end),
            rawIn.buffered()
        )
    }

    @DeleteMapping
    @TokenLevel(TokenType.FULL_ACCESS)
    fun deleteFile(
        @ShouldLogin token: UserToken,
        path: String,
    ) = withCatch {
        val pp = getPubPath(path)
        if (!Files.deleteIfExists(pp))
            throw ErrorCode.NO_SUCH_FILE.data(path)
    }

    @GetMapping("thumbnail")
    fun getThumbnail(path: String): File {
        val pp = getPubPath(path).toAbsolutePath()
        return DataHelper.thumbnailFile(pp.path.substring(1)).apply {
            if (!exists())
                throw ErrorCode.NO_SUCH_FILE.data(path)
        }
    }

    @GetMapping("preview")
    fun getPreview(path: String): File {
        val pp = getPubPath(path).toAbsolutePath()
        return DataHelper.previewFile(pp.path.substring(1)).apply {
            if (!exists())
                throw ErrorCode.NO_SUCH_FILE.data(path)
        }
    }

    @GetMapping("audio/info")
    fun getAudioInfo(path: String): AudioFileInfo {
        val p = getPubPath(path)
        return virtualFileService.getAudioInfo(p)
    }

    @GetMapping("video/stream/info")
    fun getVideoStreamInfo(path: String): List<HLSStreamInfo>? {
        val p = getPubPath(path)
        return fileMappingService.getVideoLiveStream(p)
    }

    @GetMapping("video/stream/{path}/{rate}/{file}")
    @ResponseEmpty
    fun getVideoStream(
        @PathVariable path: String,
        @PathVariable rate: String,
        @PathVariable file: String,
    ): File = withCatch {
        val pp = getPubPath(path)
        val vf = virtualFileService.get(pp)
            ?: throw ErrorCode.NO_SUCH_FILE.error
        DataHelper.tsFile((vf.hash ?: throw ErrorCode.NO_SUCH_FILE.error).pathSafe, rate, file)
    }

    @Async
    @GetMapping
    @ResponseEmpty
    fun getPublicFile(
        path: String,
        @RequestHeader(HttpHeaders.RANGE) rangeStr: String?,
        resp: HttpServletResponse
    ) {
        val pp = getPubPath(path)
        val vf = virtualFileService.get(pp) as VirtualFile?
            ?: throw ErrorCode.NO_SUCH_FILE.error
        val map = fileMappingService.getMapping(
            vf.hash ?: throw ErrorCode.NO_SUCH_FILE.error
        ) ?: throw ErrorCode.NO_SUCH_FILE.error

        val file = DataHelper.dataFile(map.dataPath).apply {
            if (!exists())
                throw ErrorCode.NO_SUCH_FILE.data(path)
        }
        val range = runCatching {
            if (rangeStr.isNullOrEmpty()) null
            else HttpRange.parseRanges(rangeStr).first()
        }.onFailure {
            throw ErrorCode.BAD_HEADER.data("range : $rangeStr")
        }.getOrThrow()

        val endIndex = vf.size - 1
        val start = range?.getRangeStart(endIndex) ?: 0
        val end = kotlin.math.min(range?.getRangeEnd(endIndex) ?: endIndex, endIndex)
        val size = end - start + 1

        resp.contentType = vf.mediaType
        if (range != null)
            resp.status = HttpStatus.PARTIAL_CONTENT.value()
        resp.setContentLength(size.toInt())
        resp.setHeader(HttpHeaders.CONTENT_RANGE, "bytes $start-$end/$endIndex")

        try {
            RandomAccessFile(file, "r").use {
                val out = resp.outputStream
                val buf = ByteArray(8 * 1024)
                it.seek(start)
                var write = 0
                while (write < size) {
                    val lIn = it.read(buf)
                    if (lIn <= 0)
                        break
                    out.write(buf, 0, lIn)
                    write += lIn
                }
            }
        } catch (ioe: IOException) {
            logger.warning(ioe.message)
        }
    }

    @PostMapping("rename")
    fun rename(
        @ShouldLogin token: UserToken,
        path: String,
        name: String
    ) = withCatch {
        val src = getPubPath(path)
        val dts = src.parent.resolve(name)
        if (src.name == dts.name)
            return@withCatch
        virtualFileService.rename(src, dts.name)
    }

    companion object {
        val RangeRegex = Regex("^(\\d+)-(\\d+)/(\\d+)$")
    }
}
