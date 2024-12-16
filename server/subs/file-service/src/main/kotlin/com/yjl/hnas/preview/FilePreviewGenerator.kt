package com.yjl.hnas.preview

import org.apache.tika.mime.MediaType
import java.awt.image.BufferedImage

/**
 * @author YJL
 */
abstract class FilePreviewGenerator(
    vararg types: MediaType
) : PreviewGenerator {

    val types = HashSet<MediaType>().apply {
        addAll(types)
    }

    fun getSize(img: BufferedImage, maxSize: Int) = getSize(img.width, img.height, maxSize)

    fun getSize(w: Int, h: Int, maxSize: Int): Size {
        val aspectRatio = w.toFloat() / h
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
