package com.yjl.hnas.hls

import com.yjl.hnas.ffmpeg.FFProbeChapter
import com.yjl.hnas.ffmpeg.FFProbeRunner
import java.io.File


/**
 * @author YJL
 */
object VideoChapterHelper {

    fun getChapter(file: File): List<FFProbeChapter> {
        val r = FFProbeRunner.probe(file.path)
        return r?.chapters ?: emptyList()
    }

}
