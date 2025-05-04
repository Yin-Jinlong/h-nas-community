package io.github.yinjinlong.hnas.controller

import io.github.yinjinlong.hnas.data.ChapterInfo
import io.github.yinjinlong.hnas.data.HLSStreamInfo
import io.github.yinjinlong.hnas.data.HLSStreamList
import io.github.yinjinlong.hnas.fs.VirtualFileSystemProvider
import io.github.yinjinlong.hnas.service.FileMappingService
import io.github.yinjinlong.hnas.token.Token
import io.github.yinjinlong.hnas.utils.logger
import io.github.yinjinlong.spring.boot.annotations.ContentType
import io.github.yinjinlong.spring.boot.annotations.ResponseRaw
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.io.File
import java.net.URLDecoder

/**
 * @author YJL
 */
@Controller
@RequestMapping(API.FILE)
class CacheFileVideoController(
    virtualFileSystemProvider: VirtualFileSystemProvider,
    val fileMappingService: FileMappingService,
) : WithFS(virtualFileSystemProvider) {

    val logger=  CacheFileVideoController::class.logger()

    @GetMapping("video/chapter")
    fun getVideoChapter(
        token: Token?,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false
    ): List<ChapterInfo> {
        logger.info("getVideoChapter: ${token?.user} $path $private")
        val p = getPath(private, token?.user, path)
        return fileMappingService.getVideoChapters(p)
    }

    @GetMapping("video/streams")
    fun getVideoStreams(
        token: Token?,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false
    ): List<HLSStreamList> {
        logger.info("getVideoStreams: ${token?.user} $path $private")
        val p = getPath(private, token?.user, path)
        return fileMappingService.getVideoLiveStreams(p)
    }

    @GetMapping("video/stream/info")
    fun getVideoStreamInfo(
        token: Token?,
        @RequestParam path: String,
        @RequestParam(required = false) private: Boolean = false,
        @RequestParam codec: String,
        @RequestParam bitrate: Int
    ): HLSStreamInfo {
        logger.info("getVideoStreamInfo: ${token?.user} $path $private")
        val p = getPath(private, token?.user, path)
        return fileMappingService.getVideoLiveStreamInfo(p, codec, bitrate)
    }

    @GetMapping("video/stream/{path}/{codec}/{bitrate}/index.m3u8")
    @ContentType("video/mpegURL")
    @ResponseRaw
    fun getVideoStreamM3u8(
        token: Token?,
        @PathVariable("path") path: String,
        @RequestParam(required = false) private: Boolean = false,
        @PathVariable("codec") codec: String,
        @PathVariable("bitrate") bitrate: Int,
    ): String {
        logger.info("getVideoStreamM3u8: ${token?.user} $path $private")
        val p = getPath(private, token?.user, path)
        return fileMappingService.getVideoLiveStreamM3u8(p, codec, bitrate, private).toString()
    }

    @GetMapping("video/stream/{path}/{codec}/{bitrate}/{index}.ts")
    fun getVideoStream(
        token: Token?,
        @PathVariable("path") path: String,
        @RequestParam(required = false) private: Boolean = false,
        @PathVariable("codec") codec: String,
        @PathVariable("bitrate") bitrate: Int,
        @PathVariable("index") index: String,
    ): File {
        logger.info("getVideoStream: ${token?.user} $path $private")
        val p = getPath(private, token?.user, URLDecoder.decode(path, Charsets.UTF_8))
        return fileMappingService.getVideoLiveStreamFile(p, codec, bitrate, index)
    }

}
