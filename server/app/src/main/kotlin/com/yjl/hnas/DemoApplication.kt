package com.yjl.hnas

import com.yjl.hnas.service.VirtualFileService
import com.yjl.hnas.service.virtual.VirtualFileSystem
import io.github.yinjinlong.spring.boot.annotations.UseWrappedReturnValue
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@UseWrappedReturnValue
@SpringBootApplication(scanBasePackages = ["io.github.yinjinlong.spring.boot", "com.yjl.hnas"])
class DemoApplication(
    val virtualFileService: VirtualFileService
) {

    @PostConstruct
    fun init() {
        VirtualFileSystem.init(virtualFileService)
    }

    @GetMapping("/**")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun def() = Unit

}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
