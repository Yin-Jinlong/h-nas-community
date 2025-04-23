package io.github.yinjinlong.hnas.ffmpeg

import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

/**
 * 命令行执行器
 * @author YJL
 */
open class CommandLineRunner(val cmd: String) {

    /**
     * 执行命令
     * @param args 参数
     * @return stdout
     */
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