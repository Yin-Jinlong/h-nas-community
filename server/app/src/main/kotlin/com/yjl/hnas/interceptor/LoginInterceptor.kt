package com.yjl.hnas.interceptor

import com.google.gson.Gson
import com.yjl.hnas.annotation.ShouldLogin
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.utils.hasAnno
import com.yjl.hnas.utils.token
import io.github.yinjinlong.spring.boot.response.JsonResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.eclipse.jetty.http.HttpHeader
import org.eclipse.jetty.http.HttpStatus
import org.springframework.web.method.HandlerMethod

/**
 * @author YJL
 */
class LoginInterceptor(
    val gson: Gson
) : HandlerMethodHandlerInterceptor() {


    fun HttpServletResponse.error(
        errorCode: ErrorCode
    ): Boolean {
        status = HttpStatus.BAD_REQUEST_400
        addHeader(HttpHeader.CONTENT_TYPE.name, "application/json")
        val json = gson.toJson(JsonResponse.clientError(errorCode.error))
        writer.write(json)
        return false
    }

    private fun shouldLogin(handler: HandlerMethod): Boolean {
        return handler.hasAnno(ShouldLogin::class)
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: HandlerMethod
    ): Boolean {
        if (shouldLogin(handler)) {
            val tr = (request.getHeader(HttpHeader.AUTHORIZATION.name)
                ?: return response.error(ErrorCode.NO_PERMISSION)
                    ).token()
            if (tr == null)
                return response.error(ErrorCode.BAD_TOKEN)
        }
        return true
    }
}