package io.github.yinjinlong.hnas.controller

import io.github.yinjinlong.hnas.fs.VirtualFileSystemProvider
import io.github.yinjinlong.hnas.service.VirtualFileService
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
class CacheFileAudioController(
    virtualFileSystemProvider: VirtualFileSystemProvider,
    val virtualFileService: VirtualFileService
) : WithFS(virtualFileSystemProvider) {

    val logger = CacheFileAudioController::class.logger()

    @GetMapping("audio/cover")
    fun getAudioCover(
        token: Token?,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false
    ): File {
        logger.info("getAudioCover: ${token?.user} $path $private")
        val p = getPath(private, token?.user, path)
        return virtualFileService.getAudioCover(p)
    }

}
