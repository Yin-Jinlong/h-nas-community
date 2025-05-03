package io.github.yinjinlong.hnas.config

import io.github.yinjinlong.hnas.redis.ObjectRedisTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory

/**
 * @author YJL
 */
@Configuration
class RedisConfig {

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): ObjectRedisTemplate =
        ObjectRedisTemplate().also {
            it.connectionFactory = redisConnectionFactory
        }

}