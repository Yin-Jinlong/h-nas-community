package com.yjl.hnas.resp

import com.yjl.hnas.error.ErrorCode
import io.github.yinjinlong.spring.boot.exception.BaseClientException
import io.github.yinjinlong.spring.boot.response.JsonResponse

/**
 * 响应生成工厂，在`application.yaml` 的`spring.responseJsonFactory` 注册
 * @author YJL
 */
@Suppress("unused")
object RespFactory {
    @JvmStatic
    fun ok(data: Any?) = RespJson(ErrorCode.OK, data)

    @JvmStatic
    fun error(e: Exception): JsonResponse {
        return RespJson(ErrorCode.SERVER_ERROR /* , e.message*/)
    }

    @JvmStatic
    fun clientError(e: BaseClientException) = RespJson(e.errorCode, e.message, e.data)

}
