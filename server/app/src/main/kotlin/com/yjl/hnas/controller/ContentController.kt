package com.yjl.hnas.controller

import com.yjl.hnas.annotation.ShouldLogin
import com.yjl.hnas.data.FileInfo
import com.yjl.hnas.error.ClientError
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.PubFileSystem
import com.yjl.hnas.fs.PubFileSystemProvider
import com.yjl.hnas.fs.UserFileSystemProvider
import com.yjl.hnas.fs.attr.FileAttribute
import com.yjl.hnas.service.FileMappingService
import com.yjl.hnas.service.UserService
import com.yjl.hnas.service.VirtualFileService
import com.yjl.hnas.tika.FileDetector
import com.yjl.hnas.utils.*
import jakarta.annotation.PostConstruct
import jakarta.servlet.ServletInputStream
import jakarta.validation.constraints.NotBlank
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.nio.file.Files


/**
 * @author YJL
 */
@Controller
@RequestMapping("/api/file")
class ContentController(
    val pubFileSystemProvider: PubFileSystemProvider,
    val userFileSystemProvider: UserFileSystemProvider,
    val userService: UserService,
    val fileMappingService: FileMappingService,
    val virtualFileService: VirtualFileService
) {
    lateinit var pubFileSystem: PubFileSystem

    @PostConstruct
    fun inti() {
        pubFileSystem = pubFileSystemProvider.getFileSystem()
    }

    @GetMapping("/files")
    fun getFiles(@NotBlank(message = "path 不能为空") path: String, token: UserToken?): List<FileInfo> {
        if (path.isBlank())
            throw ErrorCode.BAD_ARGUMENTS.error
        val p = path.trim().ifEmpty { "/" }

        val files = if (token == null) {
            virtualFileService.getFilesByParent(pubFileSystem.getPath(p).toAbsolutePath())
        } else {
            val fs = userFileSystemProvider.getFileSystem(token.data.uid)
            virtualFileService.getFilesByParent(fs.getPath(p).toAbsolutePath())
        }

        return files.map {
            it.toFileInfo(fileMappingService)
        }.sorted()
    }

    @PostMapping("/folder")
    fun createFolder(
        @RequestParam("path") path: String,
        @ShouldLogin user: UserToken,
        @RequestParam(defaultValue = "false") public: Boolean,
    ) {
        if (!userService.isLogin(user))
            throw ErrorCode.USER_NOT_LOGIN.error
        if (public) {
            val p = pubFileSystem.getPath(path).toAbsolutePath()
            pubFileSystemProvider.createDirectory(p, FileOwnerAttribute(user.data.uid))
            return
        }
        TODO()
    }

    @Async
    @PostMapping("/public/upload")
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
        val type = FileDetector.detectMagic(ins)
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
                vf.outputStream().use {
                    ins.copyTo(it)
                }
            }
            virtualFileService.createPubFile(
                token.data.uid,
                pp,
                vf.length(),
                hash = hash
            )
        }.onFailure {
            if (vf.exists() && !vf.delete())
                vf.deleteOnExit()
            throw it as? ClientError ?: ErrorCode.SERVER_ERROR.error
        }
    }

    @DeleteMapping("/public")
    fun deleteFile(
        @ShouldLogin token: UserToken,
        path: String,
    ) {
        val pp = pubFileSystem.getPath(path)
        if (!pubFileSystemProvider.deleteIfExists(pp))
            throw ErrorCode.NO_SUCH_FILE.data(path)
    }
}
