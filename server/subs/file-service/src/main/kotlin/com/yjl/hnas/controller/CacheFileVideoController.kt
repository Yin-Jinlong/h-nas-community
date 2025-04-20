package com.yjl.hnas.controller

import com.yjl.hnas.data.ChapterInfo
import com.yjl.hnas.data.HLSStreamInfo
import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.service.FileMappingService
import com.yjl.hnas.token.Token
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
    ): HLSStreamInfo? {
        val p = getPath(private, token?.user, path)
        return fileMappingService.getVideoLiveStream(p)
    }

}
