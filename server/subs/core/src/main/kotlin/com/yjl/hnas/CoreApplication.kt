package com.yjl.hnas

import com.google.gson.Gson
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.resp.RespFactory
import com.yjl.hnas.token.Token
import io.github.yinjinlong.spring.boot.annotations.UseWrappedReturnValue
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Lazy
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@UseWrappedReturnValue
@SpringBootApplication(scanBasePackages = ["io.github.yinjinlong.spring.boot", "com.yjl.hnas"])
@Lazy(false)
@RestController
abstract class CoreApplication(
    val redisTemplate: StringRedisTemplate,
    val gson: Gson
) {

    @PostConstruct
    fun init() {
        Token.init(redisTemplate, gson)
    }

    @RequestMapping("/**")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun forbidden() = RespFactory.clientError(ErrorCode.NO_PERMISSION.error)
}
