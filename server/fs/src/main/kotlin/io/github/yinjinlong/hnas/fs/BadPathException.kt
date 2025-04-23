package io.github.yinjinlong.hnas.fs

/**
 * @author YJL
 */
class BadPathException(
    msg: String? = null,
    cause: Throwable? = null,
) : IllegalArgumentException(msg, cause) {

    constructor(cause: Throwable?) : this(null, cause)
}
