package com.yjl.hnas.ffmpeg

data class FFProbeChapter(
    var id: String = "",
    var time_base: String = "",
    var start: Long = 0L,
    var start_time: Double = 0.0,
    var end: Long = 0,
    var end_time: Double = 0.0,
    var tags: FFMpegTag? = null
)
