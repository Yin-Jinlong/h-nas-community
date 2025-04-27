package io.github.yinjinlong.hnas.config

import io.github.yinjinlong.hnas.utils.getConfigValue
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory

/**
 * @author YJL
 */
@Configuration
class RedisConfig {

    @Bean
    fun redisFactory(
        config: RedisProperties
    ): RedisConnectionFactory = JedisConnectionFactory(RedisStandaloneConfiguration().apply {
        hostName = getConfigValue("redis.host", config.host)
        port = getConfigValue("redis.port", config.port.toString()).toInt()
        password = RedisPassword.of(getConfigValue("redis.password", config.password ?: ""))
        database = getConfigValue("redis.database", config.database.toString()).toInt()
    })
}