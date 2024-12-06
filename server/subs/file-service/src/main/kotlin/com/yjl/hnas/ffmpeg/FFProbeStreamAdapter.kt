package com.yjl.hnas.ffmpeg

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * @author YJL
 */
class FFProbeStreamAdapter : JsonDeserializer<FFProbeStream> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): FFProbeStream? {
        if (!json.isJsonObject)
            return null
        val obj = json.asJsonObject
        val type = obj.get("codec_type")
        return context.deserialize(
            json, when (type.asString.lowercase()) {
                "video" -> FFProbeVideoStream::class.java
                else -> typeOfT
            }
        )
    }
}