package com.yjl.hnas.controller

import com.yjl.hnas.annotation.ShouldLogin
import com.yjl.hnas.data.FileInfo
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.*
import com.yjl.hnas.fs.attr.FileAttribute
import com.yjl.hnas.service.UserService
import com.yjl.hnas.service.VirtualFileService
import com.yjl.hnas.tika.FileDetector
import com.yjl.hnas.utils.*
import jakarta.annotation.PostConstruct
import jakarta.servlet.ServletInputStream
import jakarta.validation.constraints.NotBlank
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam


/**
 * @author YJL
 */
@Controller
class ContentController(
    val pubFileSystemProvider: PubFileSystemProvider,
    val userFileSystemProvider: UserFileSystemProvider,
    val userService: UserService,
    val virtualFileService: VirtualFileService
) {
    lateinit var pubFileSystem: PubFileSystem

    @PostConstruct
    fun inti() {
        pubFileSystem = pubFileSystemProvider.getFileSystem()
    }

    @GetMapping("/api/files")
    fun getFiles(@NotBlank(message = "path 不能为空") path: String, token: UserToken?): List<FileInfo> {
        if (path.isBlank())
            throw ErrorCode.BAD_ARGUMENTS.error
        val p = path.trim().ifEmpty { "/" }

        val vp: VirtualablePath<*, *, *>

        val files = if (token == null) {
            virtualFileService.getFilesByParent(pubFileSystem.getPath(p).toAbsolutePath().also {
                vp = it
            })
        } else {
            val fs = userFileSystemProvider.getFileSystem(token.data.uid)
            virtualFileService.getFilesByParent(fs.getPath(p).toAbsolutePath().also {
                vp = it
            })
        }

        return files.map {
            it.toFileInfo(vp)
        }.sorted()
    }

    @PostMapping("/api/folder")
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
    @PostMapping("/api/file/public/upload")
    fun uploadFile(
        @ShouldLogin token: UserToken,
        @RequestHeader("Content-ID") pathBase64: String,
        @RequestHeader("Content-Length") fileSize: Long,
        @RequestHeader("Hash") sha256Base64: String,
        rawIn: ServletInputStream
    ) {
        val path = pathBase64.unBase64Url
        val pp = pubFileSystem.getPath(path)
        val ins = rawIn.buffered()
        ins.mark(1024)
        val type = FileDetector.detectMagic(ins)
        ins.reset()

        pp.bundleAttrs[FileAttribute.HASH] = FileHashAttribute(sha256Base64)
        pp.bundleAttrs[FileAttribute.TYPE] = FileTypeAttribute(type)
        val vp = pp.toVirtual()
        val vf = vp.toFile()
        try {
            val vfp = vf.parentFile
            if (!vfp.exists())
                vfp.mkdirs()
            vf.outputStream().use {
                ins.copyTo(it)
            }
            virtualFileService.createPubFile(
                token.data.uid,
                pp,
                hash = sha256Base64,
                type.type,
                type.subtype
            )
        } catch (e: Exception) {
            if (vf.exists() && !vf.delete())
                vf.deleteOnExit()
            throw ErrorCode.SERVER_ERROR.error
        }
    }
}