package com.yjl.hnas.controller

import com.yjl.hnas.data.DataHelper
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.token.Token
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.io.File

/**
 * @author YJL
 */
@Controller
@RequestMapping(API.FILE)
class CacheFileCommonController(
    virtualFileSystemProvider: VirtualFileSystemProvider,
) : WithFS(virtualFileSystemProvider) {

    @GetMapping("thumbnail")
    fun getThumbnail(
        token: Token?,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false
    ): File {
        val pp = getPath(private, token?.user, path)
        return DataHelper.thumbnailFile(pp.path.substring(1)).apply {
            if (!exists())
                throw ErrorCode.NO_SUCH_FILE.data(path)
        }
    }

    @GetMapping("preview")
    fun getPreview(
        token: Token?,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false
    ): File {
        val pp = getPath(private, token?.user, path)
        return DataHelper.previewFile(pp.path.substring(1)).apply {
            if (!exists())
                throw ErrorCode.NO_SUCH_FILE.data(path)
        }
    }

}
