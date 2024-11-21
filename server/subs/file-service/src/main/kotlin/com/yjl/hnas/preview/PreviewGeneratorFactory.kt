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

    private val jpgParm = JPEGImageWriteParam(Locale.getDefault()).apply {
        compressionMode = ImageWriteParam.MODE_EXPLICIT
        compressionQuality = 0.3f
    }

    fun getPreview(ins: InputStream, mediaType: MediaType): ByteArray? {
        val generator = generators[mediaType] ?: return null
        return kotlin.runCatching {
            ins.use {
                val img = generator.generate(ins)
                val out = ByteArrayOutputStream()
                jpgWriter().apply {
                    output = ImageIO.createImageOutputStream(out)
                    write(null, IIOImage(img, null, null), jpgParm)
                }
                out.toByteArray()
            }
        }.getOrNull()
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
