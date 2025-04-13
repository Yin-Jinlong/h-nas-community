package com.yjl.hnas.token

import com.google.gson.Gson
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.utils.base64Url
import org.springframework.data.redis.core.StringRedisTemplate
import java.time.Duration
import kotlin.random.Random

data class Token(
    val user: Uid,
    val token: String = genKey(),
) {
    private val key = "$TOKEN_PREFIX$token"

    companion object {

        const val TOKEN_PREFIX = "token:"

        private lateinit var gson: Gson
        private lateinit var redis: StringRedisTemplate

        fun init(redis: StringRedisTemplate, gson: Gson) {
            this.redis = redis
            this.gson = gson
        }

        private fun genKey() = Random.nextBytes(32).base64Url

        operator fun get(token: String): Token {
            val json = redis.opsForValue().get("$TOKEN_PREFIX$token") ?: throw ErrorCode.BAD_TOKEN.error
            return gson.fromJson(json, Token::class.java)
        }
    }

    fun register(timeout: Duration? = null) {
        val json = gson.toJson(this)
        if (timeout == null)
            redis.opsForValue().set(key, json)
        else
            redis.opsForValue().set(key, json, timeout)
    }

    fun unregister() {
        redis.delete(key)
    }
}
