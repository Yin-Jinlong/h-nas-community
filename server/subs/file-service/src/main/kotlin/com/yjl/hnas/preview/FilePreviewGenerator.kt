package com.yjl.hnas.preview

import org.apache.tika.mime.MediaType
import java.awt.image.BufferedImage
import java.io.InputStream

/**
 * @author YJL
 */
abstract class FilePreviewGenerator(
    vararg types: MediaType
) : PreviewGenerator {

    val types = HashSet<MediaType>().apply {
        addAll(types)
    }

    fun getSize(img: BufferedImage, maxSize: Int): Size {
        val aspectRatio = img.width.toFloat() / img.height
        return if (aspectRatio >= 1) {
            Size(maxSize, (maxSize / aspectRatio).toInt())
        } else {
            Size((maxSize * aspectRatio).toInt(), maxSize)
        }
    }

    data class Size(
        val width: Int,
        val height: Int
    )
}
