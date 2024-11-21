package com.yjl.hnas.controller

import com.yjl.hnas.annotation.ShouldLogin
import com.yjl.hnas.data.FileInfo
import com.yjl.hnas.entity.view.VirtualFile
import com.yjl.hnas.error.ClientError
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.PubFileSystem
import com.yjl.hnas.fs.PubFileSystemProvider
import com.yjl.hnas.fs.VirtualFileSystem
import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.fs.attr.FileAttribute
import com.yjl.hnas.service.FileMappingService
import com.yjl.hnas.service.VirtualFileService
import com.yjl.hnas.tika.FileDetector
import com.yjl.hnas.utils.*
import jakarta.annotation.PostConstruct
import jakarta.servlet.ServletInputStream
import jakarta.validation.constraints.NotBlank
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.io.File
import java.nio.file.Files
import kotlin.io.path.name

/**
 * @author YJL
 */
@Controller
@RequestMapping("/api/file/public")
class PubFileController(
    val pubFileSystemProvider: PubFileSystemProvider,
    val virtualFileSystemProvider: VirtualFileSystemProvider,
    val fileMappingService: FileMappingService,
    val virtualFileService: VirtualFileService
) {
    lateinit var pubFileSystem: PubFileSystem

    lateinit var virtualFileSystem: VirtualFileSystem

    @PostConstruct
    fun inti() {
        pubFileSystem = pubFileSystemProvider.getFileSystem()
        virtualFileSystem = virtualFileSystemProvider.getFileSystem()
    }

    @GetMapping("info")
    fun getInfo(path: String): FileInfo {
        val pp = pubFileSystem.getPath(path.deUrl)
        return virtualFileService.getFile(pp)
            ?.toFileInfo(pp.parent, fileMappingService)
            ?: throw ErrorCode.NO_SUCH_FILE.data(path)
    }

    @GetMapping("files")
    fun getFiles(@NotBlank(message = "path 不能为空") path: String): List<FileInfo> {
        if (path.isBlank())
            throw ErrorCode.BAD_ARGUMENTS.error
        val p = path.deUrl.trim().ifEmpty { "/" }

        val pp = pubFileSystem.getPath(p).toAbsolutePath()
        val files = virtualFileService.getFilesByParent(pp)

        return files.map {
            it.toFileInfo(pp, fileMappingService)
        }.sorted()
    }

    @PostMapping("folder")
    fun createFolder(
        @RequestParam("path") path: String,
        @ShouldLogin user: UserToken,
    ) {
        val p = pubFileSystem.getPath(path.deUrl).toAbsolutePath()
        pubFileSystemProvider.createDirectory(p, FileOwnerAttribute(user.data.uid))
    }

    @Async
    @PostMapping("upload")
    fun uploadFile(
        @ShouldLogin token: UserToken,
        @RequestHeader("Content-ID") pathBase64: String,
        @RequestHeader("Content-Length") fileSize: Long,
        @RequestHeader("Hash") sha256Base64: String,
        rawIn: ServletInputStream
    ) {
        val path = pathBase64.unBase64Url
        val pp = pubFileSystem.getPath(path)
        if (Files.exists(pp))
            throw ErrorCode.FILE_EXISTS.data(path)

        val hash = sha256Base64.reBase64Url

        val ins = rawIn.buffered()
        ins.mark(1024)
        val type = FileDetector.detect(ins, pp.name)
        ins.reset()
        pp.bundleAttrs[FileAttribute.TYPE] = FileTypeAttribute(type)
        pp.bundleAttrs[FileAttribute.HASH] = FileHashAttribute(hash)
        val vp = pp.toVirtual()
        val vf = vp.toFile()
        runCatching {
            if (!vf.exists()) {

                val vfp = vf.parentFile
                if (!vfp.exists())
                    vfp.mkdirs()
                try {
                    vf.outputStream().use {
                        ins.copyTo(it)
                    }
                } catch (e: Exception) {
                    if (vf.exists() && !vf.delete())
                        vf.deleteOnExit()
                }
            }
            virtualFileService.createPubFile(
                token.data.uid,
                pp,
                vf.length(),
                hash = hash
            )
        }.onFailure {
            throw it as? ClientError ?: ErrorCode.SERVER_ERROR.error
        }
    }

    @DeleteMapping
    fun deleteFile(
        @ShouldLogin token: UserToken,
        path: String,
    ) {
        val pp = pubFileSystem.getPath(path.deUrl)
        if (!pubFileSystemProvider.deleteIfExists(pp))
            throw ErrorCode.NO_SUCH_FILE.data(path)
    }

    @GetMapping("preview")
    fun getPreview(path: String): File {
        val pp = virtualFileSystem.getPath(path.deUrl).toAbsolutePath()
        return File(FileMappingService.PreviewDir, pp.path)
    }

    @GetMapping
    fun getPublicFile(path: String): File {
        val pp = pubFileSystem.getPath(path.deUrl)
        val vf = virtualFileService.getFile(pp) as VirtualFile?
            ?: throw ErrorCode.NO_SUCH_FILE.error
        try {
            val vp = vf.toVirtualPath(virtualFileSystem)
            return vp.toFile()
        } catch (e: IllegalStateException) {
            throw ErrorCode.NO_SUCH_FILE.error
        }
    }
}
