package com.yjl.hnas.hls

import com.yjl.hnas.data.DataHelper
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Frame
import java.io.File

/**
 * @author YJL
 */
object HLSGenerator {

    val bitrates = intArrayOf(300, 500, 1000, 2000, 5000)

    fun generate(file: File, time: Double, hash: String) {
        FFmpegFrameGrabber(file).use { grabber ->
            grabber.start()
            val cachePath = DataHelper.hlsPath(hash)
            val recorders = HLSRecorderList(grabber, cachePath, time, *bitrates)
            recorders.start()

            var frame: Frame? = grabber.grabFrame()
            while (frame != null) {
                try {
                    recorders.record(frame)
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
