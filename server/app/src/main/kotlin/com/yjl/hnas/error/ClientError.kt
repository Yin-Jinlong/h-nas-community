package com.yjl.hnas.error

import io.github.yinjinlong.spring.boot.exception.BaseClientException

/**
 * @author YJL
 */
class ClientError(
    code: Int,
    msg: String?,
    data: Any? = null
) : BaseClientException(
    code,
    msg,
    data
) {
    constructor(code: ErrorCode, data: Any? = null) : this(code.code, code.msg, data)
}