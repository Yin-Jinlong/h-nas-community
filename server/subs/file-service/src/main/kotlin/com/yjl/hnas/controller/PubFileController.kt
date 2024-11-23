package com.yjl.hnas.controller

import com.yjl.hnas.annotation.ShouldLogin
import com.yjl.hnas.data.FileExtraInfo
import com.yjl.hnas.data.FileInfo
import com.yjl.hnas.entity.VirtualFile
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
import java.nio.file.FileAlreadyExistsException
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
    fun getInfo(path: String): FileExtraInfo {
        val pp = pubFileSystem.getPath(path.deUrl)
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
    fun getFiles(@NotBlank(message = "path 不能为空") path: String): List<FileInfo> {
        if (path.isBlank())
            throw ErrorCode.BAD_ARGUMENTS.error
        val p = path.deUrl.trim().ifEmpty { "/" }

        val pp = pubFileSystem.getPath(p).toAbsolutePath()
        val files = virtualFileService.getByParent(pp)

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
        try {
            pubFileSystemProvider.createDirectory(p, FileOwnerAttribute(user.data.uid))
        } catch (e: FileAlreadyExistsException) {
            throw ErrorCode.FILE_EXISTS.data(path.deUrl)
        }
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
        return FileMappingService.previewFile(pp.path).apply {
            if (!exists())
                throw ErrorCode.NO_SUCH_FILE.data(path)
        }
    }

    @GetMapping
    fun getPublicFile(path: String): File {
        val pp = pubFileSystem.getPath(path.deUrl)
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
        val src = pubFileSystem.getPath(path.deUrl).toAbsolutePath()
        val dst = name.deUrl
        if (dst.contains("/") || dst.contains("\\"))
            throw ErrorCode.BAD_ARGUMENTS.data(name)
        virtualFileService.renamePublic(src, name)
    }
}
