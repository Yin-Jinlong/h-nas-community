package com.yjl.hnas.ffmpeg

data class FFProbeResult(
    var format: FFProbeFormat = FFProbeFormat(),
    var chapters: List<FFProbeChapter> = emptyList()
)
