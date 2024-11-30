package com.yjl.hnas.hls

import com.yjl.hnas.utils.mkParent
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Frame
import java.io.File
import java.io.FileWriter

/**
 * @author YJL
 */
class HLSRecorderList(
    grabber: FFmpegFrameGrabber,
    val path: String,
    val time: Double,
    vararg bitrate: Int
) : Recorder {

    private val index = File(path, "index")

    private val srcRate = grabber.videoBitrate / 1000

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
}