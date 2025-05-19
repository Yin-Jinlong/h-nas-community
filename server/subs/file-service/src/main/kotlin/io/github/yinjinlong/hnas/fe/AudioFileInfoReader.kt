package io.github.yinjinlong.hnas.fe

import com.google.gson.Gson
import com.google.gson.JsonElement
import io.github.yinjinlong.hnas.audio.AudioInfoHelper
import io.github.yinjinlong.hnas.utils.MediaSubtypeType
import org.apache.tika.mime.MediaType
import java.io.File

/**
 * @author YJL
 */
class AudioFileInfoReader(
    val gson: Gson
) : AbstractFileExtraReader(
    MediaType.audio(MediaSubtypeType.AUDIO_MP3),
    MediaType.audio(MediaSubtypeType.AUDIO_FLAC),
) {
    override fun read(file: File, type: MediaType): JsonElement {
        val r = AudioInfoHelper.getInfo(file, type)
        return gson.toJsonTree(r)
    }
}
