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

    fun generate(input: InputStream): BufferedImage

}
