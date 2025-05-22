package io.github.yinjinlong.hnas.utils

import io.github.yinjinlong.hnas.exception.DatabaseRecordNotfoundException
import io.github.yinjinlong.hnas.exception.NotMatchException

/**
 * @author YJL
 */

fun dbRecordNotFound(table: String, with: Any?): Nothing =
    throw DatabaseRecordNotfoundException(table, with?.toString())

fun notMatch(
    msg: String? = null,
    value: Any?,
    expected: Any?,
    with: Any?,
): Nothing = throw NotMatchException(msg, value, expected, with)

internal fun Any?.str(): String {
    return if (this == null) "null" else "'$this'"
}
