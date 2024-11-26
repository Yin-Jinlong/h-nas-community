package com.yjl.hnas.preview

import java.awt.image.BufferedImage
import java.io.InputStream

/**
 * @author YJL
 */
interface PreviewGenerator {

    @Throws(PreviewException::class)
    fun generate(input: InputStream, maxSize: Int): BufferedImage

}
