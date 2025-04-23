package io.github.yinjinlong.hnas.preview

import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream

/**
 * @author YJL
 */
interface PreviewGenerator {

    @Throws(PreviewException::class)
    fun generate(input: InputStream, maxSize: Int): BufferedImage

    @Throws(PreviewException::class)
    fun generate(file: File, maxSize: Int): BufferedImage =
        file.inputStream().use { generate(it, maxSize) }

}
