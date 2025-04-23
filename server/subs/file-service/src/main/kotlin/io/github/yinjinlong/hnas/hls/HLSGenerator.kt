package io.github.yinjinlong.hnas.hls

import io.github.yinjinlong.hnas.data.DataHelper
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Frame
import java.io.File

/**
 * 流媒体生成器
 * @author YJL
 */
object HLSGenerator {

    /**
     * 清晰度比特率
     */
    val bitrates = intArrayOf(1000, 2000, 5000, 10_000)

    /**
     * 生成HLS
     * @param file 源文件
     * @param time 源文件时长
     * @param hash 文件hash，用于定位缓存文件
     * @param onProgress 进度回调（3位整数）
     */
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
