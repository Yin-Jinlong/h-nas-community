package com.yjl.hnas.entity

import com.yjl.hnas.utils.base64Url
import com.yjl.hnas.utils.unBase64UrlBytes

/**
 * @author YJL
 */
data class Hash(val bytes: ByteArray = byteArrayOf()) {

    constructor(size: Int) : this(ByteArray(size))

    constructor(base64: String) : this(base64.unBase64UrlBytes)

    val base64Url by lazy { bytes.base64Url }

    val size: Int
        get() = bytes.size

    fun inputStream() = bytes.inputStream()

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
        return base64Url
    }

}
