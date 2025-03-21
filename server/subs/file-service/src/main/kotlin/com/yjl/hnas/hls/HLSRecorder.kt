package com.yjl.hnas.hls

import com.yjl.hnas.utils.mkParent
import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.ffmpeg.global.avutil
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.FFmpegLogCallback
import org.bytedeco.javacv.Frame
import java.io.File
import java.nio.file.Files

/**
 * @author YJL
 */
class HLSRecorder(
    grabber: FFmpegFrameGrabber,
    val path: String,
    /**
     * 码率kBit/s
     */
    val bitrate: Int,
    val time: Double
) : Recorder {
    val cachePath = "$path/$bitrate"
    val tmp = File(path, "$bitrate/index")
    val m3u8 = File(path, "$bitrate/index.m3u8")

    val recorder = FFmpegFrameRecorder(
        tmp,
        grabber.imageWidth,
        grabber.imageHeight,
        grabber.audioChannels
    ).apply {
        avutil.av_log_set_level(avutil.AV_LOG_INFO)
        FFmpegLogCallback.set()
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
            val encoder = NVEncoder
            if (encoder == null)
                videoCodec = avcodec.AV_CODEC_ID_H264
            else
                videoCodecName = encoder.name().string
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

        private val NVEncoder by lazy {
            kotlin.runCatching {
                // avcodec.avcodec_find_encoder_by_name("hevc_nvenc") ?:
                avcodec.avcodec_find_encoder_by_name("h264_nvenc")
            }.getOrNull()
        }

        private fun getAudioBitrate(rate: Int): Int {
            if (rate == 0)
                return AudioBitrate[3]
            return AudioBitrate.first { it >= rate }
        }

    }
}