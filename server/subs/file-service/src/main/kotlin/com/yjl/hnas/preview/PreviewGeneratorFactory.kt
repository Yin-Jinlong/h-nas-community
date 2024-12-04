package com.yjl.hnas.preview

import org.apache.tika.mime.MediaType
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.plugins.jpeg.JPEGImageWriteParam

/**
 * @author YJL
 */
class PreviewGeneratorFactory {

    private val generators = HashMap<MediaType, PreviewGenerator>()

    private fun jpgWriter() = ImageIO.getImageWritersByFormatName("jpg").next()!!

    fun getPreview(ins: InputStream, mediaType: MediaType, maxSize: Int, quality: Float): ByteArray? {
        val generator = generators[mediaType] ?: return null
        return ins.use {
            val img = generator.generate(ins, maxSize)
            val out = ByteArrayOutputStream()
            jpgWriter().apply {
                output = ImageIO.createImageOutputStream(out)
                write(
                    null, IIOImage(img, null, null),
                    JPEGImageWriteParam(Locale.getDefault()).apply {
                        compressionMode = ImageWriteParam.MODE_EXPLICIT
                        compressionQuality = quality
                    })
            }
            out.toByteArray()
        }
    }

    fun canPreview(mediaType: MediaType): Boolean {
        return generators.containsKey(mediaType)
    }

    fun registerGenerator(mediaType: MediaType, generator: PreviewGenerator) {
        val old = generators[mediaType]
        if (old != null)
            throw IllegalArgumentException("generator for $mediaType already exists")
        generators[mediaType] = generator
    }
}
