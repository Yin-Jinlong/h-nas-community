package com.yjl.hnas.controller

import com.yjl.hnas.data.AudioFileInfo
import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.service.VirtualFileService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.io.File

/**
 * @author YJL
 */
@Controller
@RequestMapping(API.PUBLIC_FILE)
class PubCacheFileAudioController(
    virtualFileSystemProvider: VirtualFileSystemProvider,
    val virtualFileService: VirtualFileService
) : WithFS(virtualFileSystemProvider) {

    @GetMapping("audio/info")
    fun getAudioInfo(path: String): AudioFileInfo {
        val p = getPubPath(path)
        return virtualFileService.getAudioInfo(p)
    }

    @GetMapping("audio/cover")
    fun getAudioCover(path: String): File {
        val p = getPubPath(path)
        return virtualFileService.getAudioCover(p)
    }

}
