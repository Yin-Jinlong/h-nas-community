package com.yjl.hnas.hls

import com.yjl.hnas.ffmpeg.FFProbeChapter
import com.yjl.hnas.ffmpeg.FFProbeRunner
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
