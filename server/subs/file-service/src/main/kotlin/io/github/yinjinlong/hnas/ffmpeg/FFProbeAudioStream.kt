package io.github.yinjinlong.hnas.ffmpeg

/**
 * @author YJL
 */
class FFProbeAudioStream : FFProbeStream() {
    var sample_fmt: String = ""
    var sample_rate: Int = 0
    var channels: Int = 2
    var channel_layout: String = ""
    var bits_per_sample: Int = 0
    var initial_padding: Long = 0
}