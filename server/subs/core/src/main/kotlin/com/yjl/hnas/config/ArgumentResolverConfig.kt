package com.yjl.hnas.config

import com.yjl.hnas.ar.TokenArgumentResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author YJL
 */
@Configuration
class ArgumentResolverConfig {

    @Bean
    fun tokenArgumentResolver() = TokenArgumentResolver()

}