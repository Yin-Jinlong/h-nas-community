package com.yjl.hnas.preview

import org.apache.tika.mime.MediaType
import java.awt.image.BufferedImage
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import javax.imageio.ImageIO

/**
 * @author YJL
 */
open class XMindPreviewGenerator : FilePreviewGenerator(
    MediaType.application("x-xmind"),
) {
    private fun ZipInputStream.thumbnailEntry(): ZipEntry {
        var entry = nextEntry
        while (entry != null) {
            if (entry.name == "Thumbnails/thumbnail.png") {
                return entry
            }
            entry = nextEntry
        }
        throw PreviewException("Bad format", IllegalStateException("No thumbnail found in xmind file"))
    }

    override fun generate(input: InputStream): BufferedImage {
        val zipIn = ZipInputStream(input)
        zipIn.thumbnailEntry()
        return ImagePreviewGenerator.INSTANCE.generate(zipIn)
    }
}
