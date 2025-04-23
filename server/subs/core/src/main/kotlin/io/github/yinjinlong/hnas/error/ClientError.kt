package io.github.yinjinlong.hnas.error

import io.github.yinjinlong.spring.boot.exception.BaseClientException
import org.springframework.http.HttpStatus

/**
 * @author YJL
 */
class ClientError(
    code: Int,
    msg: String?,
    data: Any? = null,
    status: HttpStatus = HttpStatus.BAD_REQUEST
) : BaseClientException(
    code,
    msg,
    data,
    status
) {
    constructor(code: ErrorCode, data: Any? = null) : this(code.code, code.msg, data, code.status)
}
