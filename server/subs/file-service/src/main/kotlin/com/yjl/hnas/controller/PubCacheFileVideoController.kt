package com.yjl.hnas.controller

import com.yjl.hnas.data.ChapterInfo
import com.yjl.hnas.data.HLSStreamInfo
import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.service.FileMappingService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * @author YJL
 */
@Controller
@RequestMapping(API.PUBLIC_FILE)
class PubCacheFileVideoController(
    virtualFileSystemProvider: VirtualFileSystemProvider,
    val fileMappingService: FileMappingService,
) : WithFS(virtualFileSystemProvider) {

    @GetMapping("video/chapter")
    fun getVideoChapter(path: String): List<ChapterInfo> {
        val p = getPubPath(path)
        return fileMappingService.getVideoChapters(p)
    }

    @GetMapping("video/stream/info")
    fun getVideoStreamInfo(path: String): HLSStreamInfo? {
        val p = getPubPath(path)
        return fileMappingService.getVideoLiveStream(p)
    }

}
