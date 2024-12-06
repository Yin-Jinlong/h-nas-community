package com.yjl.hnas.ffmpeg

import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject

/**
 * @author YJL
 */
class FFProbeDisposition {
    var default: Boolean = false
    var dub: Boolean = false
    var original: Boolean = false
    var comment: Boolean = false
    var lyrics: Boolean = false
    var karaoke: Boolean = false
    var forced: Boolean = false
    var hearing_impaired: Boolean = false
    var visual_impaired: Boolean = false
    var clean_effects: Boolean = false
    var attached_pic: Boolean = false
    var timed_thumbnails: Boolean = false
    var non_diegetic: Boolean = false
    var captions: Boolean = false
    var descriptions: Boolean = false
    var metadata: Boolean = false
    var dependent: Boolean = false
    var still_image: Boolean = false
    var multilayer: Boolean = false

    companion object {

        fun JsonObject.bool(key: String): Boolean {
            val v = get(key) ?: return false
            return v.asInt != 0
        }

        val TypeAdapter = JsonDeserializer<FFProbeDisposition> { json, _, _ ->
            if (!json.isJsonObject)
                return@JsonDeserializer null
            val obj = json.asJsonObject
            FFProbeDisposition().apply {
                val fields = this::class.java.declaredFields
                for (field in fields) {
                    val name = field.name
                    field.setBoolean(this, obj.bool(name))
                }
            }
        }
    }

}
