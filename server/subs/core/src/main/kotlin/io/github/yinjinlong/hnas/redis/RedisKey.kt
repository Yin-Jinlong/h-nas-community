package io.github.yinjinlong.hnas.redis

import io.github.yinjinlong.hnas.entity.Uid

/**
 * @author YJL
 */
data class RedisKey(
    val prefix: String,
    val key: String,
) {
    override fun toString(): String = "$prefix:$key"

    companion object {
        fun userTokens(uid: Uid) = RedisKey("user-tokens", uid.toString())
        fun token(key: String) = RedisKey("token", key)
    }

}
