package com.yjl.hnas.ffmpeg

/**
 * @author YJL
 */
open class FFProbeStream {
    var index: Int = 0
    var codec_name: String = ""
    var codec_long_name: String = ""
    var profile: String = ""
    var codec_type: String = ""
    var codec_tag_string: String = ""
    var codec_tag: String = ""
    var r_frame_rate: String = ""
    var avg_frame_rate: String = ""
    var time_base: String = ""
    var start_pts: Double = 0.0
    var start_time: Double = 0.0
    var extradata_size: Long = 0
    var disposition: FFProbeDisposition = FFProbeDisposition()
    var tags: Map<String, String> = mapOf()
}
