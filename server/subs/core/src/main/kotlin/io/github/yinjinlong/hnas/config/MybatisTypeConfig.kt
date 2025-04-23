package io.github.yinjinlong.hnas.config

import io.github.yinjinlong.hnas.entity.Hash
import io.github.yinjinlong.hnas.typehandler.HashTypeHandler
import org.apache.ibatis.type.TypeHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author YJL
 */
@Configuration
class MybatisTypeConfig {

    @Bean
    fun hashTypeHandler(): TypeHandler<Hash> = HashTypeHandler()

}
