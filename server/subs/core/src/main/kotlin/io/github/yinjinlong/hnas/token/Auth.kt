package io.github.yinjinlong.hnas.token

import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.redis.ObjectRedisTemplate
import io.github.yinjinlong.hnas.redis.RedisKey
import java.time.Duration

/**
 * @author YJL
 */
object Auth {

    lateinit var redis: ObjectRedisTemplate

    /**
     * 登录，默认7天
     */
    fun login(uid: Uid, role: String) = Token(uid, role).apply {
        register(Duration.ofDays(7))
    }

    /**
     * 登出
     */
    fun logout(token: Token) {
        token.unregister()
    }

    fun logoutAll(uid: Uid) {
        val key = RedisKey.userTokens(uid)
        val count = redis.opsForSet().size(key) ?: 0
        if (count > 0) {
            redis.opsForSet().pop(key, count)
        }
    }
}
