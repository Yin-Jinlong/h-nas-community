package io.github.yinjinlong.hnas.exception

import io.github.yinjinlong.hnas.utils.str

/**
 * @author YJL
 */
class NotMatchException(
    val msg: String? = null,
    val value: Any?,
    val expected: Any?,
    val with: Any?,
) : java.lang.IllegalStateException("${msg ?: "Value not match"} : expected ${expected?.str()} but got ${value.str()} with ${with.str()}")
