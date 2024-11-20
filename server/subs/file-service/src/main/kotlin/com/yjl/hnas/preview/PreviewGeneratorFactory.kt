package com.yjl.hnas.preview

import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.utils.del
import org.apache.tika.mime.MediaType
import java.io.File
import java.util.*
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.plugins.jpeg.JPEGImageWriteParam
import kotlin.io.path.name

/**
 * @author YJL
 */
class PreviewGeneratorFactory(
    val previewFileGenerator: PreviewFileGenerator = DefaultPreviewFileGenerator
) {

    private val generators = HashMap<MediaType, PreviewGenerator>()

    private val jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next()!!

    private val jpgParm = JPEGImageWriteParam(Locale.getDefault()).apply {
        compressionMode = ImageWriteParam.MODE_EXPLICIT
        compressionQuality = 0.9f
    }

    fun getPreview(path: VirtualPath, mediaType: MediaType): File? {
        val generator = generators[mediaType] ?: return null
        val file = previewFileGenerator.generate(path.name, mediaType)
        if (file.exists() && file.length() > 4)
            return file
        try {
            path.toFile().inputStream().use { ins ->
                val img = generator.generate(ins)
                file.apply {
                    val p = parentFile
                    if (!p.exists())
                        p.mkdirs()
                }.outputStream().use { outs ->
                    jpgWriter.output = ImageIO.createImageOutputStream(outs)
                    jpgWriter.write(null, IIOImage(img, null, null), jpgParm)
                }
            }
        } catch (e: Exception) {
            if (file.exists())
                file.del()
            throw e
        }
        return file
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

    interface PreviewFileGenerator {
        fun generate(name: String, mediaType: MediaType): File
    }

    companion object {
        val DefaultPreviewFileGenerator = object : PreviewFileGenerator {
            override fun generate(name: String, mediaType: MediaType): File {
                return File("cache/缩略图", "$mediaType/$name.jpg")
            }
        }
    }
}
