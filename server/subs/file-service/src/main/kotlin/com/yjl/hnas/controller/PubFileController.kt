package com.yjl.hnas.controller

import com.yjl.hnas.annotation.ShouldLogin
import com.yjl.hnas.data.FileExtraInfo
import com.yjl.hnas.data.FileInfo
import com.yjl.hnas.entity.VirtualFile
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.*
import com.yjl.hnas.service.FileMappingService
import com.yjl.hnas.service.VirtualFileService
import com.yjl.hnas.utils.*
import jakarta.servlet.ServletInputStream
import jakarta.validation.constraints.NotBlank
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.io.File
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files

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

    @GetMapping("info")
    fun getInfo(path: String): FileExtraInfo {
        val pp = getPubPath(path)
        val vf = virtualFileService.get(pp)
            ?: throw ErrorCode.NO_SUCH_FILE.data(path)
        if (vf.hash != null) {
            val fm = fileMappingService.getMapping(vf.hash!!)
                ?: throw ErrorCode.NO_SUCH_FILE.data(path)
            return FileExtraInfo(
                preview = if (fm.preview)
                    fileMappingService.getPreview(fm)
                else null,
                type = fm.type,
                subType = fm.subType
            )
        } else {
            return FileExtraInfo(
                type = "folder",
                subType = "folder"
            )
        }
    }

    @GetMapping("files")
    fun getFiles(@NotBlank(message = "path 不能为空") path: String): List<FileInfo> = withCatch {
        if (path.isBlank())
            throw ErrorCode.BAD_ARGUMENTS.error
        val p = path.trim().ifEmpty { "/" }

        val pp = getPubPath(p)
        val files = virtualFileService.getByParent(pp)

        files.map {
            it.toFileInfo(pp, fileMappingService)
        }.sorted()
    }

    @PostMapping("folder")
    fun createFolder(
        @RequestParam("path") path: String,
        @ShouldLogin user: UserToken,
    ) {
        val p = getPubPath(path)
        try {
            Files.createDirectory(p, FileOwnerAttribute(user.data.uid))
        } catch (e: FileAlreadyExistsException) {
            throw ErrorCode.FILE_EXISTS.data(path)
        }
    }

    @Async
    @PostMapping("upload")
    fun uploadFile(
        @ShouldLogin token: UserToken,
        @RequestHeader("Content-ID") pathBase64: String,
        @RequestHeader("Hash") sha256Base64: String,
        @RequestHeader("Content-Range") range: String,
        rawIn: ServletInputStream
    ) {
        TODO()
    }

    @DeleteMapping
    fun deleteFile(
        @ShouldLogin token: UserToken,
        path: String,
    ) {
        val pp = getPubPath(path)
        if (!Files.deleteIfExists(pp))
            throw ErrorCode.NO_SUCH_FILE.data(path)
    }

    @GetMapping("preview")
    fun getPreview(path: String): File {
        val pp = getPubPath(path).toAbsolutePath()
        return FileMappingService.previewFile(pp.path).apply {
            if (!exists())
                throw ErrorCode.NO_SUCH_FILE.data(path)
        }
    }

    @GetMapping
    fun getPublicFile(path: String): File {
        val pp = getPubPath(path)
        val vf = virtualFileService.get(pp) as VirtualFile?
            ?: throw ErrorCode.NO_SUCH_FILE.error
        val map = fileMappingService.getMapping(
            vf.hash ?: throw ErrorCode.NO_SUCH_FILE.error
        ) ?: throw ErrorCode.NO_SUCH_FILE.error
        return FileMappingService.dataFile(map.dataPath).apply {
            if (!exists())
                throw ErrorCode.NO_SUCH_FILE.data(path)
        }
    }

    @PostMapping("rename")
    fun rename(
        @ShouldLogin token: UserToken,
        path: String,
        name: String
    ) {
        val src = getPubPath(path)
        TODO()
    }

    companion object {
        val RangeRegex = Regex("^(\\d+)-(\\d+)/(\\d+)$")
    }
}
