package io.github.yinjinlong.hnas.config

import com.google.gson.JsonElement
import io.github.yinjinlong.hnas.typehandler.JsonTypeHandler
import org.apache.ibatis.type.TypeHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author YJL
 */
@Configuration
class MybatisJsonTypeConfig {

    @Bean
    fun jsonTypeHandler(): TypeHandler<JsonElement> = JsonTypeHandler()

}
