package io.github.yinjinlong.hnas.config

import io.github.yinjinlong.hnas.data.DataHelper
import io.github.yinjinlong.hnas.option.DataOption
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