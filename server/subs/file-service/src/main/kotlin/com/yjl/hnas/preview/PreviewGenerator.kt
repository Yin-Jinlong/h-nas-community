package com.yjl.hnas.preview

import java.awt.image.BufferedImage
import java.io.InputStream

/**
 * @author YJL
 */
interface PreviewGenerator {

    companion object {
        const val MAX_SIZE = 400
    }

    @Throws(PreviewException::class)
    fun generate(input: InputStream): BufferedImage

}
