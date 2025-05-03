package io.github.yinjinlong.hnas.token

import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.redis.ObjectRedisTemplate
import io.github.yinjinlong.hnas.redis.RedisKey
import io.github.yinjinlong.hnas.utils.base64Url
import java.time.Duration
import kotlin.random.Random

/**
 * @author YJL
 */
data class Token(
    val user: Uid,
    val role: String,
    val token: String = genKey(),
) {
    private val redisUserKey = RedisKey.userTokens(user)
    private val redisKey = RedisKey.token(token)

    companion object {
        private lateinit var redis: ObjectRedisTemplate

        fun init(redis: ObjectRedisTemplate) {
            this.redis = redis
            Auth.redis = redis
        }

        /**
         * 随机生成token，32字节，一般不会生成重复的
         */
        private fun genKey() = Random.nextBytes(32).base64Url

        operator fun get(token: String): Token? {
            val key = RedisKey.token(token)
            val value = redis.opsForValue().get(key) as Token? ?: return null
            val has = redis.opsForSet().isMember(RedisKey.userTokens(value.user), token)
            return if (has == true) value else {
                redis.delete(key)
                null
            }
        }
    }

    /**
     * 注册token
     */
    fun register(timeout: Duration) {
        redis.opsForValue().set(redisKey, this, timeout)
        redis.opsForSet().add(redisUserKey, token)
    }

    /**
     * 注销token
     */
    fun unregister() {
        redis.delete(redisKey)
        redis.opsForSet().remove(redisUserKey, token)
    }
}
