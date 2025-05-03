package io.github.yinjinlong.hnas.redis

import org.springframework.data.redis.core.RedisTemplate

/**
 * @author YJL
 */
class ObjectRedisTemplate : RedisTemplate<RedisKey, Any>() {

    init {
        keySerializer = RedisKeySerializer
        hashKeySerializer = RedisKeySerializer
        valueSerializer = ObjectSerializer
        hashValueSerializer = ObjectSerializer
    }

}