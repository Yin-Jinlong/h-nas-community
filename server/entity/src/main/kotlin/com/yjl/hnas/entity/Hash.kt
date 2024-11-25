package com.yjl.hnas.entity

import com.yjl.hnas.utils.base64Url
import com.yjl.hnas.utils.unBase64UrlBytes
import java.nio.ByteBuffer

/**
 * @author YJL
 */
data class Hash(val bytes: ByteArray = byteArrayOf()) {

    constructor(size: Int) : this(ByteArray(size))

    constructor(base64: String) : this(base64.unBase64UrlBytes)

    val base64Url by lazy { bytes.base64Url }

    val pathSafe by lazy {
        val sb = StringBuilder()
        val buf = byteBuffer()
        while (buf.remaining() >= 8) {
            val v = buf.getShort().toInt() and 0xFFFF
            sb.append(v.str())
        }
        sb.toString()
    }

    val size: Int
        get() = bytes.size

    fun inputStream() = bytes.inputStream()

    fun byteBuffer() = ByteBuffer.wrap(bytes)

    operator fun get(index: Int): Byte = bytes[index]

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other !is Hash)
            return false

        return bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }

    override fun toString(): String {
        return pathSafe
    }

    companion object {

        private val DefaultChars =
            "%&+0123456789@[]abcdefghijklmnopqrstuvwxyz"
                .toCharArray()

        private fun Int.str(chars: CharArray = DefaultChars): String = StringBuilder().apply {
            var v = this@str
            while (v > 0) {
                val i = v % chars.size
                append(chars[i])
                v /= chars.size
            }
            reverse()
        }.toString()
    }

}
