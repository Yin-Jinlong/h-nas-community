package io.github.yinjinlong.hnas.controller

import io.github.yinjinlong.hnas.data.DataHelper
import io.github.yinjinlong.hnas.error.ErrorCode
import io.github.yinjinlong.hnas.fs.VirtualFileSystemProvider
import io.github.yinjinlong.hnas.token.Token
import io.github.yinjinlong.hnas.utils.logger
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

    val logger = CacheFileCommonController::class.logger()

    @GetMapping("thumbnail")
    fun getThumbnail(
        token: Token?,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false
    ): File {
        logger.info("getThumbnail ${token?.user} $path $private")
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
        logger.info("getPreview ${token?.user} $path $private")
        val pp = getPath(private, token?.user, path)
        return DataHelper.previewFile(pp.path.substring(1)).apply {
            if (!exists())
                throw ErrorCode.NO_SUCH_FILE.data(path)
        }
    }

}
