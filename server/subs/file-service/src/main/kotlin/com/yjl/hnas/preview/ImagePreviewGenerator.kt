package com.yjl.hnas.preview

import org.apache.tika.mime.MediaType
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO

/**
 * @author YJL
 */
open class ImagePreviewGenerator : FilePreviewGenerator(
    setOf(
        MediaType.image("png"),
        MediaType.image("jpeg"),
    )
) {
    override fun generate(input: InputStream): BufferedImage {
        val img = ImageIO.read(input)
        val size = getSize(img)
        val res = BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB)
        res.graphics.drawImage(img, 0, 0, size.width, size.height, null)
        return res
    }
}
