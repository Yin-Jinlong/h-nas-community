package io.github.yinjinlong.hnas.preview

import io.github.yinjinlong.hnas.utils.MediaSubtypeType
import jakarta.transaction.NotSupportedException
import org.apache.pdfbox.Loader
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.tika.mime.MediaType
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream

/**
 * @author YJL
 */
open class PDFPreviewGenerator : FilePreviewGenerator(
    MediaType.application(MediaSubtypeType.APPLICATION_PDF),
) {
    override fun generate(input: InputStream, maxSize: Int) = throw NotSupportedException()

    override fun generate(file: File, maxSize: Int): BufferedImage = try {
        val doc = Loader.loadPDF(file)
        if (doc.numberOfPages < 1)
            throw RuntimeException("pdf页数为0")
        val page = doc.getPage(0)
        val renderer = PDFRenderer(doc)
        val width = page.bBox.width
        val height = page.bBox.height
        val size = getSize(width.toInt(), height.toInt(), maxSize)
        val s = size.width.toFloat() / width
        BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB).apply {
            renderer.renderPageToGraphics(0, graphics as Graphics2D, s)
        }
    } catch (e: Exception) {
        throw PreviewException("预览pdf失败：${e.message}")
    }
}
