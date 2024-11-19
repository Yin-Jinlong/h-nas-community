package com.yjl.hnas.resp

import io.github.yinjinlong.spring.boot.response.JsonResponse
import com.yjl.hnas.error.ErrorCode
import io.github.yinjinlong.spring.boot.annotations.JsonIgnored
import org.springframework.http.HttpStatus

/**
 * 响应对象
 */
@Suppress("unused")
open class RespJson(
    val code: Int,
    val msg: String?,
    val data: Any?,
    @field:JsonIgnored
    override var status: HttpStatus = HttpStatus.OK
) : JsonResponse {

    constructor(code: ErrorCode, data: Any? = null, status: HttpStatus = code.status) : this(
        code.code,
        code.msg,
        data,
        status
    )

}
