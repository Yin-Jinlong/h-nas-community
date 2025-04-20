package com.yjl.hnas.controller

import com.yjl.hnas.data.AudioFileInfo
import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.service.VirtualFileService
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
class CacheFileAudioController(
    virtualFileSystemProvider: VirtualFileSystemProvider,
    val virtualFileService: VirtualFileService
) : WithFS(virtualFileSystemProvider) {

    @GetMapping("audio/info")
    fun getAudioInfo(
        token: Token?,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false
    ): AudioFileInfo {
        val p = getPath(private, token?.user, path)
        return virtualFileService.getAudioInfo(p)
    }

    @GetMapping("audio/cover")
    fun getAudioCover(
        token: Token?,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false
    ): File {
        val p = getPath(private, token?.user, path)
        return virtualFileService.getAudioCover(p)
    }

}
