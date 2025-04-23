package io.github.yinjinlong.hnas.hls

import io.github.yinjinlong.hnas.ffmpeg.FFProbeChapter
import io.github.yinjinlong.hnas.ffmpeg.FFProbeRunner
import java.io.File


/**
 * @author YJL
 */
object VideoChapterHelper {

    /**
     * 获取视频的章节信息
     */
    fun getChapter(file: File): List<FFProbeChapter> {
        val r = FFProbeRunner.probe(file.path)
        return r.chapters
    }

}
