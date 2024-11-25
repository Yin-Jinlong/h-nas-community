package com.yjl.hnas.usertype

import com.yjl.hnas.entity.Hash
import org.hibernate.engine.jdbc.BinaryStream
import org.hibernate.type.SqlTypes
import org.hibernate.type.descriptor.WrapperOptions
import org.hibernate.type.descriptor.java.AbstractClassJavaType
import org.hibernate.type.descriptor.java.DataHelper
import org.hibernate.type.descriptor.jdbc.AdjustableJdbcType
import org.hibernate.type.descriptor.jdbc.JdbcType
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.sql.Blob
import kotlin.reflect.KClass

/**
 * @author YJL
 */
class HashJavaType : AbstractClassJavaType<Hash>(Hash::class.java) {

    companion object {
        val INSTANCE = HashJavaType()
    }

    private fun KClass<*>.sub(type: Class<*>) = java.isAssignableFrom(type)

    override fun getRecommendedJdbcType(indicators: JdbcTypeIndicators): JdbcType {
        val descriptor = indicators.getJdbcType(indicators.resolveJdbcTypeCode(SqlTypes.VARBINARY))
        return if (descriptor is AdjustableJdbcType)
            descriptor.resolveIndicatedType(indicators, this)
        else
            descriptor
    }

    // converted from ByteArrayJavaType
    override fun fromString(string: CharSequence?): Hash? {
        if (string == null) {
            return null
        }
        require(string.length % 2 == 0) { "The string is not a valid string representation of a binary content." }
        val bytes = ByteArray(string.length / 2)
        for (i in bytes.indices) {
            val hexStr = string.subSequence(i * 2, (i + 1) * 2).toString()
            bytes[i] = hexStr.toInt(16).toByte()
        }
        return Hash(bytes)
    }

    override fun <X : Any?> unwrap(value: Hash?, type: Class<X>, options: WrapperOptions): X? {
        if (value == null)
            return null
        return when {
            ByteArray::class.sub(type) -> value.bytes

            Array<Byte>::class.sub(type) -> Array(value.size) { value[it] }

            InputStream::class.sub(type) -> value.inputStream()

            BinaryStream::class.sub(type) -> BinStream(value.bytes)

            Blob::class.sub(type) -> options.lobCreator.createBlob(value.bytes)

            else -> throw unknownUnwrap(type)
        } as X
    }

    override fun <X : Any?> wrap(value: X, options: WrapperOptions?): Hash? {
        if (value == null)
            return null
        return when (value) {
            is ByteArray -> Hash(value)

            is Array<*> -> (value as? Array<Byte>)?.toByteArray()?.let { Hash(it) }

            is InputStream -> Hash(value.readBytes())

            else -> Hash(
                if (value is Blob || DataHelper.isNClob(value.javaClass)) {
                    DataHelper.extractBytes((value as Blob).binaryStream)
                } else throw unknownWrap(value!!::class.java)
            )
        }
    }

    private class BinStream(val data: ByteArray) : BinaryStream {
        override fun getInputStream() = ByteArrayInputStream(data)

        override fun getBytes() = data

        override fun getLength(): Long = data.size.toLong()

        override fun release() = Unit
    }
}
