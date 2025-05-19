package io.github.yinjinlong.hnas.fe

import com.google.gson.JsonElement
import org.apache.tika.mime.MediaType
import java.io.File

/**
 * @author YJL
 */
interface FileExtraReader {

    val types: Set<MediaType>

    fun read(file: File, type: MediaType): JsonElement

}
