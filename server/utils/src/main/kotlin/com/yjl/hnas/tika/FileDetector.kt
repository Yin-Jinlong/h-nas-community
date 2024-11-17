package com.yjl.hnas.tika

import org.apache.tika.detect.Detector
import org.apache.tika.detect.TextDetector
import org.apache.tika.metadata.Metadata
import org.apache.tika.metadata.TikaCoreProperties
import org.apache.tika.mime.MediaType
import org.apache.tika.mime.MimeTypes
import java.io.InputStream

/**
 * @author YJL
 */
object FileDetector : Detector {
    private fun readResolve(): Any = FileDetector

    val mimeTypes: MimeTypes = MimeTypes.getDefaultMimeTypes()
    val textDetector = TextDetector()

    private val emptyMetadata = Metadata()

    override fun detect(input: InputStream?, metadata: Metadata?): MediaType {
        val type = mimeTypes.detect(input, metadata ?: emptyMetadata)
        return if (type == MediaType.OCTET_STREAM)
            textDetector.detect(input, metadata)
        else type
    }

    fun detectMagic(input: InputStream): MediaType {
        return mimeTypes.detect(input, emptyMetadata)
    }

    fun detectName(name: String): MediaType {
        return mimeTypes.detect(null, Metadata().apply {
            this.set(TikaCoreProperties.RESOURCE_NAME_KEY, name)
        })
    }

    fun maybeText(input: InputStream): Boolean {
        return textDetector.detect(input, emptyMetadata).type == "text"
    }
}