package io.github.yinjinlong.hnas.fe

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import org.apache.tika.mime.MediaType
import java.io.File

/**
 * @author YJL
 */
class FileExtraReaderHelper {

    val readers = HashMap<MediaType, FileExtraReader>()

    fun getExtra(file: File, type: MediaType): JsonElement {
        return (readers[type] ?: return JsonNull.INSTANCE).read(file, type)
    }

    fun registerReader(mediaType: MediaType, reader: FileExtraReader) {
        val old = readers[mediaType]
        if (old != null)
            throw IllegalArgumentException("reader for $mediaType already exists")
        readers[mediaType] = reader
    }
}
