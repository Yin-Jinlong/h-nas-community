package io.github.yinjinlong.hnas

import com.google.gson.Gson
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Lazy
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.RestController

@RestController
@SpringBootApplication
@Lazy(false)
class UserApplication(
    redisTemplate: StringRedisTemplate,
    gson: Gson
) : CoreApplication(redisTemplate, gson)

fun main(vararg args: String) {
    runApplication<UserApplication>(*args)
}
