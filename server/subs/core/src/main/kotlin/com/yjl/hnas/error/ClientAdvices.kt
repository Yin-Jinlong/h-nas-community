package com.yjl.hnas.error

import com.yjl.hnas.resp.RespJson
import io.github.yinjinlong.spring.boot.response.JsonResponse
import org.springframework.core.annotation.Order
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.HandlerMethodValidationException

/**
 * @author YJL
 */
@ControllerAdvice
@Order(-1)
class ClientAdvices {

    @ExceptionHandler(NoSuchFileException::class)
    fun noSuchFileError(e: NoSuchFileException) = RespJson(ErrorCode.NO_SUCH_FILE)

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun missHeaderError(e: MissingRequestHeaderException) = RespJson(ErrorCode.BAD_HEADER, e.headerName)

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun missParamError(e: MissingServletRequestParameterException) = RespJson(ErrorCode.BAD_REQUEST, e.message)

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun paramValidationError(e: HandlerMethodValidationException): JsonResponse {
        return RespJson(ErrorCode.BAD_ARGUMENTS, mutableListOf<String>().apply {
            e.allErrors.forEach {
                it.defaultMessage?.let(::add)
            }
        })
    }

}