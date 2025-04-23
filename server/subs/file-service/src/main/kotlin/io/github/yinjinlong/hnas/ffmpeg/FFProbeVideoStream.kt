package io.github.yinjinlong.hnas.ffmpeg

/**
 * @author YJL
 */
class FFProbeVideoStream : FFProbeStream() {
    var width: Int = 0
    var height: Int = 0
    var coded_width: Int = 0
    var coded_height: Int = 0
    var closed_captions: Long = 0
    var film_grain: Long = 0
    var has_b_frames: Long = 0
    var sample_aspect_ratio: String = ""
    var display_aspect_ratio: String = ""
    var pix_fmt: String = ""
    var level: Int = 0
    var color_range: String = ""
    var color_space: String = ""
    var color_transfer: String = ""
    var color_primaries: String = ""
    var chroma_location: String = ""
    var refs: Int = 0
    var view_ids_available = ""
    var view_pos_available = ""
}