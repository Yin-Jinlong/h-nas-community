package io.github.yinjinlong.hnas.preview

import io.github.yinjinlong.hnas.utils.MediaSubtypeType
import org.apache.tika.mime.MediaType
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream

/**
 * @author YJL
 */
open class VideoPreviewGenerator : FilePreviewGenerator(
    MediaType.video(MediaSubtypeType.VIDEO_MP4),
    MediaType.video(MediaSubtypeType.VIDEO_MKV_MATROSKA),
) {
    private val imagePreviewGenerator = ImagePreviewGenerator.INSTANCE

    override fun generate(input: InputStream, maxSize: Int): BufferedImage =
        throw UnsupportedOperationException()

    override fun generate(file: File, maxSize: Int): BufferedImage = kotlin.runCatching {
        imagePreviewGenerator.gen(FFmpegFrameGrabber(file).use { grabber ->
            grabber.start()
            grabber.timestamp = (grabber.lengthInTime / 100)
            val frame = grabber.grabImage() ?: throw IllegalStateException("视频预览失败: 无法获取视频帧")
            val converter = Java2DFrameConverter()
            converter.convert(frame)
        }, maxSize)
    }.onFailure {
        throw PreviewException("视频预览失败: ${it.message}")
    }.getOrThrow()
}
