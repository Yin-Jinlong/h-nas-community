package com.yjl.hnas.preview

import org.apache.tika.mime.MediaType
import java.awt.image.BufferedImage
import java.io.InputStream

/**
 * @author YJL
 */
abstract class FilePreviewGenerator(
    val types: Set<MediaType>
) : PreviewGenerator {

    fun getSize(img: BufferedImage): Size {
        val aspectRatio = img.width.toFloat() / img.height
        return if (aspectRatio >= 1) {
            Size(PreviewGenerator.MAX_SIZE, (PreviewGenerator.MAX_SIZE / aspectRatio).toInt())
        } else {
            Size((PreviewGenerator.MAX_SIZE * aspectRatio).toInt(), PreviewGenerator.MAX_SIZE)
        }
    }

    abstract override fun generate(input: InputStream): BufferedImage

    data class Size(
        val width: Int,
        val height: Int
    )
}
