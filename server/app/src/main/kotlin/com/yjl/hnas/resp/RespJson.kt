package com.yjl.hnas.resp

import io.github.yinjinlong.spring.boot.response.JsonResponse
import com.yjl.hnas.error.ErrorCode

/**
 * 响应对象
 */
@Suppress("unused")
open class RespJson(
    val code: Int,
    val msg: String?,
    val data: Any?
) : JsonResponse {

    constructor(code: ErrorCode, data: Any? = null) : this(code.code, code.msg, data)

}