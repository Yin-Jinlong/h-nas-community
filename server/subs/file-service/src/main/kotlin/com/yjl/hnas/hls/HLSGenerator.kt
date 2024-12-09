package com.yjl.hnas.hls

import com.yjl.hnas.data.DataHelper
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Frame
import java.io.File

/**
 * @author YJL
 */
object HLSGenerator {

    val bitrates = intArrayOf(1000, 2000, 5000, 10_000)

    fun generate(file: File, time: Double, hash: String, onProgress: (Int) -> Unit) {
        FFmpegFrameGrabber(file).use { grabber ->
            grabber.setOption("hwaccel", "auto")
            grabber.start()
            val cachePath = DataHelper.hlsPath(hash)
            val recorders = HLSRecorderList(file, grabber, cachePath, time, *bitrates)
            recorders.start()

            val len = grabber.lengthInTime
            var frame: Frame? = grabber.grabFrame()
            while (frame != null) {
                try {
                    recorders.record(frame)
                    onProgress((1000L * frame.timestamp / len).toInt())
                    frame = grabber.grabFrame()
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw e
                }
            }

            recorders.close()
            recorders.finish()
        }
    }

}
