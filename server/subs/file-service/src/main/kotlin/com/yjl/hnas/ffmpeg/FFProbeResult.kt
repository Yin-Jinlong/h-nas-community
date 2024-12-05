package com.yjl.hnas.ffmpeg

data class FFProbeResult(
    var chapters: List<FFProbeChapter> = emptyList()
)
