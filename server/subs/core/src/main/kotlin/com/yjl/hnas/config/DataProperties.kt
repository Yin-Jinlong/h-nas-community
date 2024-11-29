package com.yjl.hnas.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author YJL
 */
@ConfigurationProperties(prefix = "app.data")
data class DataProperties(
    val root: String = "data",
    val cacheRoot: String = "cache"
)
