package com.yjl.hnas.controller

import com.yjl.hnas.data.FileInfo
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.service.VirtualFileService
import com.yjl.hnas.utils.virtual
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
    val virtualFileService: VirtualFileService
) {

//    @Autowired
//    lateinit var contentService: ContentService

//    @GetMapping("/api/files")
//    fun getFiles(folder: String?): List<FileInfo> {
//        return contentService.getFiles(folder ?: "").map {
//            it.toFileInfo(folderService)
//        }
//    }

    @GetMapping("/api/files")
    fun getFiles(@NotBlank(message = "path 不能为空") path: String, uid: Uid?): List<FileInfo> {
        if (path.isBlank())
            throw ErrorCode.BAD_ARGUMENTS.error
        val p = path.trim().ifEmpty { "/" }

        val vp = virtualFileService.toVirtualPath(uid, p)
        return virtualFileService.getFiles(vp).map {
            it.virtual(vp).toFileInfo()
        }
    }

    @PostMapping("/api/folder")
    fun createFolder(@RequestParam("path") path: String, user: Uid) {
//        virtualFileService.getVFile()
//        virtualFileService.createFolder()
    }
//
//    @DeleteMapping("/api/file/{path}")
//    fun deleteFile(@PathVariable path: String) {
//        contentService.deleteFile(path)
//    }
//
//    @GetMapping("/api/thumbnail/{path}")
//    fun getThumbnail(@PathVariable path: String): File {
//        val file = contentService.getFile(path) ?: throw ClientError(ErrorCode.NO_SUCH_FILE)
//        if (!contentService.hasValidThumbnail(file))
//            contentService.createThumbnail(file)
//        return contentService.getThumbnail(file.md5)
//    }
}