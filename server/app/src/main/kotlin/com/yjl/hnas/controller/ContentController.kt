package com.yjl.hnas.controller

import com.yjl.hnas.annotation.ShouldLogin
import com.yjl.hnas.data.FileInfo
import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.*
import com.yjl.hnas.fs.attr.FileOwnerAttribute
import com.yjl.hnas.service.UserService
import com.yjl.hnas.service.VirtualFileService
import com.yjl.hnas.token.Token
import com.yjl.hnas.utils.toFileInfo
import jakarta.annotation.PostConstruct
import jakarta.validation.constraints.NotBlank
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
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
    fun getFiles(@NotBlank(message = "path 不能为空") path: String, token: Token<UserInfo>?): List<FileInfo> {
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
        @ShouldLogin user: Token<UserInfo>,
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

}