package com.yjl.hnas.preview

import org.apache.tika.mime.MediaType
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO

/**
 * @author YJL
 */
open class ImagePreviewGenerator : FilePreviewGenerator(
    MediaType.image("png"),
    MediaType.image("jpeg"),
    MediaType.image("webp"),
) {
    companion object {
        val INSTANCE = ImagePreviewGenerator()
    }

    fun gen(img: BufferedImage, maxSize: Int): BufferedImage {
        val size = getSize(img, maxSize)
        val res = BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB)
        res.graphics.drawImage(img, 0, 0, size.width, size.height, null)
        return res
    }

    override fun generate(input: InputStream, maxSize: Int): BufferedImage {
        return gen(ImageIO.read(input), maxSize)
    }

}
