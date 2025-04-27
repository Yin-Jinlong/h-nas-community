package io.github.yinjinlong.hnas.utils

/**
 * 默认值 < 环境变量 < jvm 参数
 * @param key a.b
 * @author YJL
 */
fun getConfigValue(key: String, def: String): String {
    return System.getProperty(key) ?: System.getenv(key.envName) ?: def
}

private val String.envName: String
    get() = split(".").joinToString("_").uppercase()
