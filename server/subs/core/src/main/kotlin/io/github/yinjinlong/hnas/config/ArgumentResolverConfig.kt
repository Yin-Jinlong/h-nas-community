package io.github.yinjinlong.hnas.config

import io.github.yinjinlong.hnas.ar.TokenArgumentResolver
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