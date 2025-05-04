package io.github.yinjinlong.hnas.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * @author YJL
 */
fun KClass<*>.logger(): Logger = LoggerFactory.getLogger(this.java)
