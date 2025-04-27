package io.github.yinjinlong.hnas.ffmpeg

import com.google.gson.GsonBuilder
import io.github.yinjinlong.hnas.utils.mkParent
import org.bytedeco.ffmpeg.ffmpeg
import java.io.File
import java.net.JarURLConnection

/**
 * @author YJL
 */
object FFProbeRunner : CommandLineRunner("bin/ffprobe") {

    private val platform: String by lazy {
        val os = System.getProperty("os.name").lowercase()
        when {
            os.startsWith("windows") -> "windows"
            os.startsWith("mac os x") -> "macosx"
            os.contains("linux") -> "linux"
            else -> throw UnsupportedOperationException("Unsupported operating system: $os")
        }
    }

    private val arch: String by lazy {
        val arch = System.getProperty("os.arch").lowercase()
        when {
            arch.startsWith("arm") -> "arm"
            arch.startsWith("x86") -> "x86"
            arch.startsWith("amd64") -> "x86_64"
            else -> throw UnsupportedOperationException("Unsupported architecture: $arch")
        }
    }

    private fun extractProbe() {
        val file = File("bin/ffprobe" + if (platform == "windows") ".exe" else "")
        if (file.exists())
            return
        file.mkParent()

        val dir = "org/bytedeco/ffmpeg/$platform-$arch"

        val loader = ffmpeg::class.java.classLoader
        val url = loader.getResource(dir)
            ?: throw IllegalArgumentException("$dir not found")

        val bin = File("./bin")
        bin.mkParent()

        val i = url.openConnection() as JarURLConnection
        for (e in i.jarFile.entries()) {
            if (e.name.endsWith("/") || e.size == 0L || !e.name.startsWith(dir) || e.name.contains("jni"))
                continue
            val name = e.name.split("/").last()
            bin.resolve(name).also { f ->
                f.outputStream().use { out ->
                    i.jarFile.getInputStream(e).use { it.copyTo(out) }
                }
            }.setExecutable(true, false)
        }
    }

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

    /**
     * 获取文媒体信息
     */
    fun probe(file: String): FFProbeResult {
        extractProbe()
        return gson.fromJson(
            run(*args.toTypedArray(), file).decodeToString(),
            FFProbeResult::class.java
        )
    }


}