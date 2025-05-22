package io.github.yinjinlong.hnas.exception

import io.github.yinjinlong.hnas.utils.str

/**
 * @author YJL
 */
class DatabaseRecordNotfoundException(
    val table: String,
    val with: String?,
) : java.lang.IllegalStateException("Record not found in table '$table' with ${with.str()}")
