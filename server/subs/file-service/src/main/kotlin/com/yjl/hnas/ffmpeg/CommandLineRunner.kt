package com.yjl.hnas.ffmpeg

import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

/**
 * @author YJL
 */
open class CommandLineRunner(val cmd: String) {
    fun run(vararg args: String): ByteArray {
        val pb = ProcessBuilder(cmd, *args)
        val process = pb.start()
        val r = ByteArrayOutputStream()
        try {
            process.inputStream.use {
                it.copyTo(r)
            }
            process.waitFor(10, TimeUnit.SECONDS)
        } finally {
            process.destroy()
        }
        return r.toByteArray()
    }
}