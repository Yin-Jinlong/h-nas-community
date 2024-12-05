package com.yjl.hnas.preview

import org.apache.tika.mime.MediaType
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.image.BufferedImage
import java.io.InputStream

/**
 * @author YJL
 */
open class VideoPreviewGenerator : FilePreviewGenerator(
    MediaType.video("mp4"),
    MediaType.video("x-matroska"),
) {
    private val imagePreviewGenerator = ImagePreviewGenerator.INSTANCE

    override fun generate(input: InputStream, maxSize: Int): BufferedImage = kotlin.runCatching {
        imagePreviewGenerator.gen(FFmpegFrameGrabber(input).use { grabber ->
            grabber.start()
            val frame = grabber.grabImage() ?: throw IllegalStateException("视频预览失败: 无法获取第一帧")
            val converter = Java2DFrameConverter()
            converter.convert(frame)
        }, maxSize)
    }.onFailure {
        throw PreviewException("视频预览失败: ${it.message}")
    }.getOrThrow()
}
