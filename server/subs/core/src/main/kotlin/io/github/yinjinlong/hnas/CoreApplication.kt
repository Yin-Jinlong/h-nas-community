package io.github.yinjinlong.hnas

import io.github.yinjinlong.hnas.error.ErrorCode
import io.github.yinjinlong.hnas.redis.ObjectRedisTemplate
import io.github.yinjinlong.hnas.resp.RespFactory
import io.github.yinjinlong.hnas.token.Token
import io.github.yinjinlong.spring.boot.annotations.UseWrappedReturnValue
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@UseWrappedReturnValue
@SpringBootApplication(scanBasePackages = ["io.github.yinjinlong.spring.boot", "io.github.yinjinlong.hnas"])
@Lazy(false)
@RestController
@EnableDiscoveryClient
abstract class CoreApplication(
    val redisTemplate: ObjectRedisTemplate,
) {

    @PostConstruct
    fun init() {
        Token.init(redisTemplate)
    }

    @RequestMapping("/**")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun forbidden() = RespFactory.clientError(ErrorCode.NO_PERMISSION.error)
}
