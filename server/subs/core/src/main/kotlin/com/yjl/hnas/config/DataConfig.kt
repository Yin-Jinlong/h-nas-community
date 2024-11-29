package com.yjl.hnas.config

import com.yjl.hnas.data.DataHelper
import com.yjl.hnas.option.DataOption
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

/**
 * @author YJL
 */
@Configuration
@EnableConfigurationProperties(DataProperties::class)
@Lazy(false)
class DataConfig(
    val dataProperties: DataProperties
) {

    @PostConstruct
    fun init() {
        DataHelper.init(DataOption(dataProperties.root, dataProperties.cacheRoot))
    }

}