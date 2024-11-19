package com.yjl.hnas

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.web.bind.annotation.RestController

@RestController
@EnableDiscoveryClient
@SpringBootApplication
class GatewayApplication

fun main(vararg args: String) {
    runApplication<GatewayApplication>(*args)
}
