package com.yjl.hnas.ffmpeg

import com.google.gson.GsonBuilder

/**
 * @author YJL
 */
object FFProbeRunner : CommandLineRunner("ffprobe") {

    val args = listOf(
        "-v", "quiet",
        "-print_format", "json",
        "-show_error",
        "-show_format",
        "-show_streams",
        "-show_chapters",
    )

    val gson = GsonBuilder()
        .registerTypeAdapter(FFProbeStream::class.java, FFProbeStreamAdapter())
        .registerTypeAdapter(FFProbeDisposition::class.java, FFProbeDisposition.TypeAdapter)
        .create()

    fun probe(file: String): FFProbeResult {
        return gson.fromJson(
            run(*args.toTypedArray(), file).decodeToString(),
            FFProbeResult::class.java
        )
    }

}