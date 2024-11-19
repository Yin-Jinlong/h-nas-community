package com.yjl.hnas

import com.google.gson.Gson
import com.yjl.hnas.token.Token
import io.github.yinjinlong.spring.boot.annotations.UseWrappedReturnValue
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Lazy

@UseWrappedReturnValue
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = ["io.github.yinjinlong.spring.boot", "com.yjl.hnas"])
@Lazy(false)
abstract class CoreApplication(
    val gson: Gson
) {

    @PostConstruct
    fun init() {
        Token.init(gson)
    }

}
