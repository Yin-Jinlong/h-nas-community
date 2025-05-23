package io.github.yinjinlong.hnas.controller

import io.github.yinjinlong.hnas.annotation.ShouldLogin
import io.github.yinjinlong.hnas.data.*
import io.github.yinjinlong.hnas.entity.Hash
import io.github.yinjinlong.hnas.entity.IUser
import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.entity.VirtualFile
import io.github.yinjinlong.hnas.error.ErrorCode
import io.github.yinjinlong.hnas.fs.VirtualFileSystemProvider
import io.github.yinjinlong.hnas.fs.VirtualPath
import io.github.yinjinlong.hnas.fs.attr.FileAttributes
import io.github.yinjinlong.hnas.service.FileMappingService
import io.github.yinjinlong.hnas.service.VirtualFileService
import io.github.yinjinlong.hnas.token.Token
import io.github.yinjinlong.hnas.utils.*
import io.github.yinjinlong.spring.boot.annotations.ResponseEmpty
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotBlank
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.compress.compressors.gzip.GzipParameters
import org.springframework.boot.context.properties.bind.DefaultValue
import org.springframework.http.*
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.attribute.FileTime
import kotlin.io.path.name

/**
 * @author YJL
 */
@Controller
@RequestMapping(API.FILE)
class FileController(
    virtualFileSystemProvider: VirtualFileSystemProvider,
    val fileMappingService: FileMappingService,
    val virtualFileService: VirtualFileService
) : WithFS(virtualFileSystemProvider) {

    val logger = FileController::class.logger()

    @GetMapping("preview/info")
    fun getFilePreview(
        token: Token?,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false,
    ): FilePreview {
        logger.info("getFilePreview: ${token?.user} $path $private")
        val pp = getPath(private, token?.user, path)
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
    fun folderChildrenCount(
        token: Token?,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false,
    ): FolderChildrenCount = withCatch {
        logger.info("folderChildrenCount: ${token?.user} $path $private")
        val pp = getPath(private, token?.user, path)
        val cc = virtualFileService.getFolderChildrenCount(pp)
        FolderChildrenCount(
            pp.path,
            cc.subCount,
            cc.subsCount
        )
    }

    @GetMapping("storage/user/usage")
    fun getUserStorageUsage(
        @ShouldLogin token: Token,
        @RequestParam uid: Uid?
    ): Long {
        if (token.role != IUser.ROLE_ADMIN)
            throw ErrorCode.NO_PERMISSION.error
        return virtualFileService.getUserStorageUsage(uid)
    }

    @GetMapping("info")
    fun getFileInfo(
        token: Token?,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false,
    ): FileInfo = withCatch {
        logger.info("getFileInfo: ${token?.user} $path $private")
        val p = path.trim().ifEmpty { "/" }

        val pp = getPath(private, token?.user, p).toAbsolutePath()
        val file = virtualFileService.get(pp)
            ?: throw ErrorCode.NO_SUCH_FILE.data(path)

        file.toFileInfo(pp, fileMappingService)
    }

    @GetMapping("files")
    fun getFiles(
        token: Token?,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false,
        type: String?
    ): List<FileInfo> = withCatch {
        logger.info("getFiles: ${token?.user} $path $private")
        val p = path.trim().ifEmpty { "/" }

        val pp = getPath(private, token?.user, p)
        val files = virtualFileService.getByParent(pp, type)

        files.map {
            it.toFileInfo(pp, fileMappingService)
        }.sorted()
    }

    @PostMapping("folder")
    fun createFolder(
        @RequestParam path: String,
        @ShouldLogin user: Token,
        @RequestParam(required = false) private: Boolean = false,
    ): Unit = withCatch {
        logger.info("createFolder: ${user.user} $path $private")
        val p = getPath(private, user.user, path)
        Files.createDirectory(p, FileOwnerAttribute(user.user))
    }

    @Async
    @PostMapping("upload")
    @Suppress("UastIncorrectHttpHeaderInspection")
    fun uploadFile(
        @ShouldLogin token: Token,
        @RequestHeader("Content-ID") pathBase64: String,
        @RequestHeader("Hash") sha256Base64: String,
        @RequestHeader("Content-Range") range: String,
        @RequestParam(required = false) private: Boolean = false,
        rawIn: ServletInputStream
    ): Boolean = withCatch {
        logger.info("uploadFile: ${token.user} $pathBase64 $sha256Base64 $range $private")
        val path = getPath(private, token.user, pathBase64.unBase64Url)
        val hash = sha256Base64.reBase64Url

        val mr = RangeRegex.matchEntire(range)
            ?: throw ErrorCode.BAD_ARGUMENTS.data(range)

        val start = mr.groupValues[1].toLong()
        val end = mr.groupValues[2].toLong()
        val size = mr.groupValues[3].toLong()

        if (end < start)
            throw ErrorCode.BAD_ARGUMENTS.data(range)

        virtualFileService.upload(
            token.user,
            path,
            Hash(hash),
            size,
            FileRange(start, end),
            rawIn.buffered()
        )
    }

    @DeleteMapping
    fun deleteFile(
        @ShouldLogin token: Token,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false,
    ) = withCatch {
        logger.info("deleteFile: ${token.user} $path $private")
        val pp = getPath(private, token.user, path)
        pp.bundledAttributes[FileAttributes.OWNER] = token.user
        pp.bundledAttributes[FileAttributes.ROLE] = token.role
        if (!Files.deleteIfExists(pp))
            throw ErrorCode.NO_SUCH_FILE.data(path)
    }

    fun downloadDir(path: VirtualPath, rootVF: VirtualFile, resp: HttpServletResponse) = try {
        resp.status = HttpStatus.OK.value()
        resp.contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE
        resp.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.builder("attachment")
                .filename("${rootVF.name}.tar.gz", Charsets.UTF_8)
                .build()
                .toString()
        )
        val root = path.parent
        TarArchiveOutputStream(GzipCompressorOutputStream(resp.outputStream, DownloadGZIPParam)).use { out ->
            out.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU)
            Files.walk(path).forEach {
                val vf = virtualFileService.get(it as VirtualPath) ?: return@forEach
                val entry = TarArchiveEntry(root.relativize(it).path.let { rp ->
                    if (vf.isFolder())
                        "$rp/"
                    else rp
                }).apply {
                    size = vf.size
                    creationTime = FileTime.fromMillis(vf.createTime.time)
                    lastModifiedTime = FileTime.fromMillis(vf.updateTime.time)
                }
                out.putArchiveEntry(entry)
                if (vf.isFile())
                    Files.copy(it, out)
                out.closeArchiveEntry()
            }
            out.finish()
        }
    } catch (e: IOException) {
        logger.warn(e.message)
    }

    @Async
    @GetMapping
    @ResponseEmpty
    fun getFile(
        token: Token?,
        @NotBlank
        @RequestParam path: String,
        @RequestHeader(HttpHeaders.RANGE) rangeStr: String?,
        @DefaultValue("false") download: Boolean,
        @RequestParam(required = false) private: Boolean = false,
        resp: HttpServletResponse
    ) {
        logger.info("getFile: ${token?.user} $path $private")
        val pp = getPath(private, token?.user, path)
        val vf = virtualFileService.get(pp) as VirtualFile?
            ?: throw ErrorCode.NO_SUCH_FILE.error
        val map = fileMappingService.getMapping(
            vf.hash ?: if (download) return downloadDir(pp, vf, resp)
            else throw ErrorCode.NO_SUCH_FILE.error
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

        val len = vf.size
        val start = range?.getRangeStart(len) ?: 0
        val end = kotlin.math.min(range?.getRangeEnd(len) ?: (len - 1), len - 1)
        val size = end - start + 1

        if (range != null)
            resp.status = HttpStatus.PARTIAL_CONTENT.value()
        resp.contentType = "${map.type}/${map.subType}"
        resp.setContentLengthLong(size)
        resp.setHeader(HttpHeaders.CONTENT_RANGE, "bytes $start-$end/$len")

        if (download) {
            resp.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.builder("attachment")
                    .filename(vf.name, Charsets.UTF_8)
                    .build()
                    .toString()
            )
        }

        try {
            RandomAccessFile(file, "r").use {
                val out = resp.outputStream
                val buf = ByteArray(Buffers.DOWNLOAD_BUFFER_SIZE)
                it.seek(start)
                var remain = size
                while (remain > 0) {
                    val lIn = it.read(
                        buf,
                        0,
                        if (remain < buf.size) remain.toInt() else buf.size
                    )
                    if (lIn <= 0)
                        break
                    out.write(buf, 0, lIn)
                    remain -= lIn
                }
            }
        } catch (ioe: IOException) {
            logger.warn(ioe.message)
        }
    }

    @PostMapping("rename")
    fun rename(
        @ShouldLogin token: Token,
        @RequestParam path: String,
        @RequestParam name: String,
        @RequestParam(required = false) private: Boolean = false,
    ) = withCatch {
        val src = getPath(private, token.user, path)
        val dts = src.parent.resolve(name)
        if (src.name == dts.name)
            return@withCatch
        src.bundledAttributes[FileAttributes.OWNER] = token.user
        src.bundledAttributes[FileAttributes.ROLE] = token.role
        virtualFileService.rename(src, dts.name)
    }

    @GetMapping("search")
    fun search(
        token: Token?,
        @RequestParam name: String,
        @RequestParam(required = false) lastPath: String? = null,
        @RequestParam(required = false) private: Boolean = false,
    ): List<FileInfo> {
        logger.info("search: ${token?.user} $name $lastPath $private")
        return virtualFileService.search(
            if (private) token?.user ?: throw ErrorCode.BAD_ARGUMENTS.error else 0,
            name,
            lastPath?.let { getPath(private, token?.user, it) }
        ).map { (vf, path) ->
            vf.toFileInfo(path.parent, fileMappingService)
        }
    }

    companion object {
        val RangeRegex = Regex("^(\\d+)-(\\d+)/(\\d+)$")

        val DownloadGZIPParam = GzipParameters().apply {
            compressionLevel = 5
            bufferSize = Buffers.DOWNLOAD_GZIP_BUFFER_SIZE
        }
    }
}
