package io.github.yinjinlong.hnas.redis

/**
 * @author YJL
 */
object RedisKeySerializer : PrefixRedisSerializer<RedisKey> {
    override fun serialize(value: RedisKey?): ByteArray? {
        if (value == null) return null
        return PrefixRedisSerializer.wrap(value.prefix.toByteArray(), value.key.toByteArray())
    }

    override fun deserialize(bytes: ByteArray?): RedisKey? {
        if (bytes == null) return null
        val (prefixBytes, keyBytes) = PrefixRedisSerializer.unwrap(bytes)
        return RedisKey(String(prefixBytes), String(keyBytes))
    }
}