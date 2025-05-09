package io.github.yinjinlong.hnas.preview

import io.github.yinjinlong.hnas.utils.MediaSubtypeType
import org.apache.tika.mime.MediaType
import java.awt.image.BufferedImage
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * @author YJL
 */
open class XMindPreviewGenerator : FilePreviewGenerator(
    MediaType.application(MediaSubtypeType.APPLICATION_X_MIND),
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

    override fun generate(input: InputStream, maxSize: Int): BufferedImage {
        val zipIn = ZipInputStream(input)
        zipIn.thumbnailEntry()
        return ImagePreviewGenerator.INSTANCE.generate(zipIn, maxSize)
    }
}
