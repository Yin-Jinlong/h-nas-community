package io.github.yinjinlong.hnas.redis

import com.google.gson.Gson

/**
 * @author YJL
 */
object ObjectSerializer : PrefixRedisSerializer<Any> {

    private val gson = Gson()

    override fun serialize(value: Any?): ByteArray? {
        if (value == null) return null
        val valueClassBytes = value.javaClass.name.toByteArray()
        val valueBytes = when (value.javaClass) {
            Boolean::class.java,
            Byte::class.java,
            UByte::class.java,
            Short::class.java,
            UShort::class.java,
            Int::class.java,
            UInt::class.java,
            Long::class.java,
            ULong::class.java,
            Float::class.java,
            Double::class.java,
            String::class.java ->
                value.toString()

            else -> gson.toJson(value)
        }.toByteArray()
        return PrefixRedisSerializer.wrap(valueClassBytes, valueBytes)
    }

    override fun deserialize(bytes: ByteArray?): Any? {
        if (bytes == null) return null
        val (valueClassBytes, valueBytes) = PrefixRedisSerializer.unwrap(bytes)
        val valueClass = Class.forName(String(valueClassBytes))
        val value = String(valueBytes)
        return when (valueClass) {
            Boolean::class.java -> value.toBoolean()
            Byte::class.java -> value.toByte()
            UByte::class.java -> value.toUByte()
            Short::class.java -> value.toShort()
            UShort::class.java -> value.toUShort()
            Int::class.java -> value.toInt()
            UInt::class.java -> value.toUInt()
            Long::class.java -> value.toLong()
            ULong::class.java -> value.toULong()
            Float::class.java -> value.toFloat()
            Double::class.java -> value.toDouble()
            String::class.java -> value

            else -> gson.fromJson(value, valueClass)
        }
    }
}