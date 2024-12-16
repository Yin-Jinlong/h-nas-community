package com.yjl.hnas.preview

import com.yjl.hnas.utils.MediaSubtypeType
import org.apache.tika.mime.MediaType
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO

/**
 * @author YJL
 */
open class ImagePreviewGenerator : FilePreviewGenerator(
    MediaType.image(MediaSubtypeType.IMAGE_PNG),
    MediaType.image(MediaSubtypeType.IMAGE_JPEG),
    MediaType.image(MediaSubtypeType.IMAGE_WEBP),
) {
    companion object {
        val INSTANCE = ImagePreviewGenerator()
    }

    fun gen(img: BufferedImage, maxSize: Int): BufferedImage {
        val size = getSize(img, maxSize)
        return BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB).apply {
            graphics.drawImage(img, 0, 0, size.width, size.height, null)
        }
    }

    override fun generate(input: InputStream, maxSize: Int): BufferedImage {
        return gen(ImageIO.read(input), maxSize)
    }

}
