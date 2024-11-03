package com.yjl.hnas.config

import com.google.gson.Gson
import com.yjl.hnas.interceptor.LoginInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author YJL
 */
@Configuration
class InterceptorConfig {

    @Bean
    fun loginInterceptor(gson: Gson) = LoginInterceptor(gson)

}