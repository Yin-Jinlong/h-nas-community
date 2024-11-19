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

    fun detect(input: InputStream, fileName: String): MediaType {
        return mimeTypes.detect(input, Metadata().apply {
            set(TikaCoreProperties.RESOURCE_NAME_KEY, fileName)
        })
    }

    override fun detect(input: InputStream?, metadata: Metadata?): MediaType {
        return mimeTypes.detect(input, metadata ?: emptyMetadata)
    }

    fun maybeText(input: InputStream): Boolean {
        return textDetector.detect(input, emptyMetadata).type == "text"
    }
}