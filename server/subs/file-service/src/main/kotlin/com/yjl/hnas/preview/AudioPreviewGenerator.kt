package com.yjl.hnas.preview

import com.yjl.hnas.audio.AudioInfoHelper
import com.yjl.hnas.utils.del
import org.apache.tika.mime.MediaType
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

/**
 * @author YJL
 */
open class AudioPreviewGenerator : FilePreviewGenerator(
    MediaType.audio("mpeg"),
    MediaType.audio("x-flac"),
) {
    private val imagePreviewGenerator = ImagePreviewGenerator.INSTANCE

    override fun generate(input: InputStream, maxSize: Int): BufferedImage = kotlin.runCatching {
        val tmpFile = File.createTempFile("${System.currentTimeMillis()}", ".tmp")
        try {
            tmpFile.outputStream().use {
                input.copyTo(it)
            }
            val coverData = AudioInfoHelper.getCoverData(tmpFile)
                ?: throw PreviewException("没有封面")
            val ins = ByteArrayInputStream(coverData)
            imagePreviewGenerator.generate(ins, maxSize)
        } finally {
            tmpFile.del()
        }
    }.onFailure {
        throw PreviewException("音频预览失败: ${it.message}")
    }.getOrThrow()
}
