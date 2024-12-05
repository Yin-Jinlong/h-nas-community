package com.yjl.hnas.hls

import com.yjl.hnas.utils.mkParent
import net.bramp.ffmpeg.FFprobe
import org.bytedeco.ffmpeg.avcodec.AVPacket
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Frame
import java.io.File
import java.io.FileWriter

/**
 * @author YJL
 */
class HLSRecorderList(
    file: File,
    grabber: FFmpegFrameGrabber,
    val path: String,
    val time: Double,
    vararg bitrate: Int
) : Recorder {

    private val index = File(path, "index")

    private val srcRate = (grabber.videoBitrate / 1000).let {
        if (it > 0)
            it
        else
            grabber.tryGetVideoBitrate(file).toInt()
    }

    private val recorders = bitrate.filter {
        it < srcRate
    }.map {
        HLSRecorder(grabber, path, it, time)
    }.toMutableList().apply {
        add(HLSRecorder(grabber, path, srcRate, time))
    }

    private fun loop(block: HLSRecorder.() -> Unit) {
        for (recorder in recorders)
            recorder.block()
    }

    override fun start() = loop {
        start()
    }

    override fun record(frame: Frame) = loop {
        record(frame)
    }

    override fun finish() = loop {
        finish()
    }.also {
        index.mkParent()
        FileWriter(index).use { out ->
            for (recorder in recorders) {
                out.write(recorder.bitrate.toString())
                out.write("\n")
            }
        }
    }

    override fun close() = loop {
        close()
    }

    companion object {

        private fun getTotalBitrate(file: File): Long {
            val r = FFprobe().probe(file.path)
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
    }
}