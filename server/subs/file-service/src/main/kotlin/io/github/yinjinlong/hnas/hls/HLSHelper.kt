package io.github.yinjinlong.hnas.hls

import io.github.yinjinlong.hnas.data.DataHelper
import io.github.yinjinlong.hnas.data.HLSStream
import io.github.yinjinlong.hnas.data.HLSStreamList
import io.github.yinjinlong.hnas.entity.Hash
import io.github.yinjinlong.hnas.ffmpeg.FFProbeRunner
import org.bytedeco.ffmpeg.avcodec.AVPacket
import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Frame
import java.io.File

/**
 * @author YJL
 */
object HLSHelper {
    private fun nvidia(name: String) = "${name}_nvenc"
    private fun amd(name: String) = "${name}_amf"
    private fun vulkan(name: String) = "${name}_vulkan"
    private fun vaapi(name: String) = "${name}_vaapi"
    private fun qsv(name: String) = "${name}_qsv"

    private fun getAvailableCodec(vararg codecs: String): String {
        for (codec in codecs) {
            if (avcodec.avcodec_find_encoder_by_name(codec) != null)
                return codec
        }
        throw IllegalArgumentException("No available codec")
    }

    private val h264Encoder = getAvailableCodec(
        nvidia("h264"),
        amd("h264"),
        vulkan("h264"),
        vaapi("h264"),
        qsv("h264"),
        "h264_qsv",
        "libx264",
    )

    private val hevcEncoder = getAvailableCodec(
        nvidia("hevc"),
        amd("hevc"),
        vulkan("hevc"),
        "hevc_d3d12va",
        vaapi("hevc"),
        qsv("hevc"),
        "libx265",
    )

    private val av1Encoder = getAvailableCodec(
        nvidia("av1"),
        amd("av1"),
        vaapi("av1"),
        qsv("av1"),
        "libaom-av1",
    )

    val codecs = arrayOf(h264Encoder, hevcEncoder, av1Encoder)
    val bitrates = arrayOf(1_000, 2_000, 5_000, 10_000, 20_000)

    private fun getTotalBitrate(file: File): Long {
        val r = FFProbeRunner.probe(file.path)
        return r.format.bit_rate / 1000
    }

    private fun FFmpegFrameGrabber.getAudioPacket(): AVPacket? {
        var r = grabPacket()
        while (r != null && r.stream_index() != audioStream) {
            r = grabPacket()
        }
        return r
    }

    private fun FFmpegFrameGrabber.getAudioBitrateIn1s(): Long {
        if (!hasAudio())
            return 0
        val timebase = formatContext.streams(audioStream).time_base()
        val num = timebase.num()
        val den = timebase.den()
        var size = 0L
        var dts = 0L

        while (dts * num < den) {
            val packet = getAudioPacket() ?: break
            size += packet.size()
            dts = packet.dts()
        }

        return size / dts
    }

    private fun FFmpegFrameGrabber.tryGetVideoBitrate(file: File): Long {
        val tt = getTotalBitrate(file)
        val a = getAudioBitrateIn1s()
        frameNumber = 0
        return tt - a
    }

    fun getHLSStreamInfoList(
        file: File,
    ): List<HLSStreamList> = FFmpegFrameGrabber(file).use { grabber ->
        grabber.setOption("hwaccel", "auto")
        grabber.start()

        val srcRate = (grabber.videoBitrate / 1000).let {
            if (it > 0)
                it
            else
                grabber.tryGetVideoBitrate(file).toInt()
        }

        val width = grabber.imageWidth
        val height = grabber.imageHeight
        val rates = bitrates.filter {
            it < srcRate
        }

        codecs.map { codec ->
            HLSStreamList(
                codec = codec,
                streams = rates.map {
                    HLSStream(
                        width = width,
                        height = height,
                        bitrate = it
                    )
                }
            )
        }
    }

    fun generate(
        videoFile: File,
        hash: Hash,
        codec: String,
        bitrate: Int,
        duration: Double,
        onProgress: (Int) -> Unit
    ) {
        FFmpegFrameGrabber(videoFile).use { grabber ->
            grabber.setOption("hwaccel", "auto")
            grabber.start()
            val cachePath = DataHelper.hlsPath(hash.pathSafe)
            val recorder = HLSRecorder(grabber, cachePath, codec, bitrate, duration)
            recorder.start()

            val len = grabber.lengthInTime
            var frame: Frame? = grabber.grabFrame()
            while (frame != null) {
                try {
                    recorder.record(frame)
                    onProgress((1000L * frame.timestamp / len).toInt())
                    frame = grabber.grabFrame()
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw e
                }
            }

            recorder.close()
            recorder.finish()
        }
    }

}