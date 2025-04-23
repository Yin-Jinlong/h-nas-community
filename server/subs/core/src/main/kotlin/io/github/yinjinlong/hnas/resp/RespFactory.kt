package io.github.yinjinlong.hnas.resp

import io.github.yinjinlong.hnas.error.ErrorCode
import io.github.yinjinlong.spring.boot.exception.BaseClientException
import io.github.yinjinlong.spring.boot.response.JsonResponse
import org.springframework.http.HttpStatus

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
        return RespJson(ErrorCode.SERVER_ERROR, status = HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @JvmStatic
    fun clientError(e: BaseClientException) = RespJson(e.errorCode, e.message, e.data, e.status)

}
