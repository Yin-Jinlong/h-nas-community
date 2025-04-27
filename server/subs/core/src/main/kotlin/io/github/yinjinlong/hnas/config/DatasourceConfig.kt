package io.github.yinjinlong.hnas.config

import com.zaxxer.hikari.HikariDataSource
import io.github.yinjinlong.hnas.utils.getConfigValue
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

/**
 * @author YJL
 */
@Configuration
class DatasourceConfig {

    @Bean
    fun datasource(
        props: DataSourceProperties
    ): DataSource = HikariDataSource().apply {
        jdbcUrl = getConfigValue("jdbc.url", props.url)
        username = getConfigValue("jdbc.username", props.username)
        password = getConfigValue("jdbc.password", props.password)
        driverClassName = getConfigValue("jdbc.driverClassName", props.driverClassName)
    }

}