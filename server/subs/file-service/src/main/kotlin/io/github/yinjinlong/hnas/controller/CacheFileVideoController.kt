package io.github.yinjinlong.hnas.controller

import io.github.yinjinlong.hnas.data.ChapterInfo
import io.github.yinjinlong.hnas.data.HLSStreamInfo
import io.github.yinjinlong.hnas.fs.VirtualFileSystemProvider
import io.github.yinjinlong.hnas.service.FileMappingService
import io.github.yinjinlong.hnas.token.Token
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author YJL
 */
@Controller
@RequestMapping(API.FILE)
class CacheFileVideoController(
    virtualFileSystemProvider: VirtualFileSystemProvider,
    val fileMappingService: FileMappingService,
) : WithFS(virtualFileSystemProvider) {

    @GetMapping("video/chapter")
    fun getVideoChapter(
        token: Token?,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false
    ): List<ChapterInfo> {
        val p = getPath(private, token?.user, path)
        return fileMappingService.getVideoChapters(p)
    }

    @GetMapping("video/stream/info")
    fun getVideoStreamInfo(
        token: Token?,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false
    ): List<HLSStreamInfo> {
        val p = getPath(private, token?.user, path)
        return fileMappingService.getVideoLiveStream(p)
    }

}
