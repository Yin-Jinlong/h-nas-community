package com.yjl.hnas.controller

import com.yjl.hnas.data.DataHelper
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.VirtualFileSystemProvider
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.io.File

/**
 * @author YJL
 */
@Controller
@RequestMapping(API.PUBLIC_FILE)
class PubCacheFileCommonController(
    virtualFileSystemProvider: VirtualFileSystemProvider,
) : WithFS(virtualFileSystemProvider) {

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

}
