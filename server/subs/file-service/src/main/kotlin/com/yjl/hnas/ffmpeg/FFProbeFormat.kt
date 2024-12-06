package com.yjl.hnas.ffmpeg

/**
 * @author YJL
 */
class FFProbeFormat {
    var filename: String = ""
    var nb_streams: Int = 0
    var nb_programs: Int = 0
    var nb_stream_groups: Int = 0
    var format_name: String = ""
    var format_long_name: String = ""
    var start_time: Double = 0.0
    var duration: Double = 0.0
    var size: Long = 0
    var bit_rate: Long = 0
    var probe_score: Int = 0
    var tags: Map<String, String> = mapOf()
}