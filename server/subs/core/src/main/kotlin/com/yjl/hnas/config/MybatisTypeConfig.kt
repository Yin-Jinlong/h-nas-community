package com.yjl.hnas.config

import com.yjl.hnas.entity.Hash
import com.yjl.hnas.typehandler.HashTypeHandler
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
