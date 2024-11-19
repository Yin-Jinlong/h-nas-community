package com.yjl.hnas

import com.google.gson.Gson
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Lazy
import org.springframework.web.bind.annotation.RestController

@RestController
@EnableDiscoveryClient
@SpringBootApplication
@Lazy(false)
class FileApplication(
    gson: Gson
) : CoreApplication(gson)

fun main(vararg args: String) {
    runApplication<FileApplication>(*args)
}
