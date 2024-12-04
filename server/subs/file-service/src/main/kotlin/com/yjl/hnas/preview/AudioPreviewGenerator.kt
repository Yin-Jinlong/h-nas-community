package com.yjl.hnas.preview

import com.yjl.hnas.utils.del
import com.yjl.hnas.utils.getCoverFrame
import org.apache.tika.mime.MediaType
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.mp3.MP3File
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

/**
 * @author YJL
 */
open class AudioPreviewGenerator : FilePreviewGenerator(
    MediaType.audio("mpeg"),
) {
    private val imagePreviewGenerator = ImagePreviewGenerator.INSTANCE

    override fun generate(input: InputStream, maxSize: Int): BufferedImage = kotlin.runCatching {
        val tmpFile = File.createTempFile("${System.currentTimeMillis()}", ".tmp")
        try {
            tmpFile.outputStream().use {
                input.copyTo(it)
            }
            val af = AudioFileIO.readMagic(tmpFile)
            if (af !is MP3File || !af.hasID3v2Tag())
                throw PreviewException("没有id3v2标签")
            val tag = af.iD3v2Tag!!
            val coverFrame = tag.getCoverFrame()
                ?: throw PreviewException("没有封面")
            val ins = ByteArrayInputStream(coverFrame.imageData)
            imagePreviewGenerator.generate(ins, maxSize)
        } finally {
            tmpFile.del()
        }
    }.onFailure {
        throw PreviewException("音频预览失败: ${it.message}")
    }.getOrThrow()
}
