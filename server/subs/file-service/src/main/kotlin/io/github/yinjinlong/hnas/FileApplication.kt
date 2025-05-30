package io.github.yinjinlong.hnas

import io.github.yinjinlong.hnas.redis.ObjectRedisTemplate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Lazy
import org.springframework.web.bind.annotation.RestController

@RestController
@SpringBootApplication
@Lazy(false)
class FileApplication(
    redisTemplate: ObjectRedisTemplate,
) : CoreApplication(redisTemplate)

fun main(vararg args: String) {
    System.setProperty("nacos.logging.default.config.enabled", "false")
    runApplication<FileApplication>(*args)
}
