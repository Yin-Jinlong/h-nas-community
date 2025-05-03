package io.github.yinjinlong.hnas.redis

import org.springframework.data.redis.serializer.RedisSerializer
import java.nio.ByteBuffer

/**
 * @author YJL
 */
interface PrefixRedisSerializer<T> : RedisSerializer<T> {
    companion object {
        fun wrap(prefix: ByteArray, value: ByteArray): ByteArray {
            val buf = ByteBuffer.allocate(prefix.size + value.size + 2)
            buf.putShort(prefix.size.toShort())
            buf.put(prefix)
            buf.put(value)
            return buf.array()
        }

        fun unwrap(bytes: ByteArray): Pair<ByteArray, ByteArray> {
            val buf = ByteBuffer.wrap(bytes)
            val prefixSize = buf.short.toInt()
            val prefix = ByteArray(prefixSize)
            val value = ByteArray(bytes.size - prefixSize - 2)
            buf.get(prefix)
            buf.get(value)
            return prefix to value
        }
    }
}