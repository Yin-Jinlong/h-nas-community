package io.github.yinjinlong.hnas.hls

import io.github.yinjinlong.hnas.utils.mkParent
import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.ffmpeg.global.avutil
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Frame
import java.io.File
import java.nio.file.Files

/**
 * @author YJL
 */
class HLSRecorder(
    grabber: FFmpegFrameGrabber,
    val path: String,
    val codec: String,
    /**
     * 码率kBit/s
     */
    val bitrate: Int,
    val time: Double
) : Recorder {
    val cachePath = File("$path/$codec/$bitrate").absolutePath
    val tmp = File(path, "$codec/$bitrate/index")
    val m3u8 = File(path, "$codec/$bitrate/index.m3u8")

    val recorder = FFmpegFrameRecorder(
        tmp,
        grabber.imageWidth,
        grabber.imageHeight,
        grabber.audioChannels
    ).apply {
        format = "hls"
        setOption("hls_time", "$time")
        setOption("hls_list_size", "0")
        setOption("hls_flags", "delete_segments")
        setOption("hls_delete_threshold", "1")
        setOption("hls_segment_type", "mpegts")
        setOption("hls_segment_filename", "$cachePath/%d.ts")

        frameRate = grabber.frameRate
        if (grabber.hasVideo()) {
            videoBitrate = bitrate * 1000
            videoCodecName = codec
        }

        if (grabber.hasAudio()) {
            sampleFormat = avutil.AV_SAMPLE_FMT_FLTP
            sampleRate = grabber.sampleRate
            audioCodec = avcodec.AV_CODEC_ID_AAC
            audioBitrate = getAudioBitrate(grabber.audioBitrate)
        }
    }

    override fun start() {
        tmp.mkParent()
        recorder.start()
    }

    override fun record(frame: Frame) {
        recorder.record(frame)
    }

    override fun close() {
        recorder.stop()
    }

    override fun finish() {
        Files.move(tmp.toPath(), m3u8.toPath())
    }

    companion object {

        private val AudioBitrate = arrayOf(
            32_000,
            48_000,
            56_000,
            64_000,
            96_000,
            128_000,
            Int.MAX_VALUE,
        )

        private fun getAudioBitrate(rate: Int): Int {
            if (rate == 0)
                return AudioBitrate[3]
            return AudioBitrate.first { it >= rate }
        }

    }
}