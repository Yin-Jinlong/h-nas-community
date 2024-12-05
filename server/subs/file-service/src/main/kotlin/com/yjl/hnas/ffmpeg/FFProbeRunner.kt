package com.yjl.hnas.ffmpeg

import com.google.gson.Gson

/**
 * @author YJL
 */
object FFProbeRunner : CommandLineRunner("ffprobe") {

    val args = listOf(
        "-v", "quiet",
        "-print_format", "json",
        "-show_chapters"
    )

    val gson = Gson()

    fun probe(file: String): FFProbeResult? {
        return gson.fromJson(
            run(*args.toTypedArray(), file).decodeToString(),
            FFProbeResult::class.java
        )
    }

}