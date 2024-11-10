package com.yjl.hnas.ar

import com.yjl.hnas.annotation.ShouldLogin
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.token.Token
import com.yjl.hnas.utils.hasAnno
import com.yjl.hnas.utils.token
import org.eclipse.jetty.http.HttpHeader
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * @author YJL
 */
class TokenArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == Token::class.java
    }

    private fun shouldLogin(parameter: MethodParameter): Boolean {
        return parameter.hasAnno(ShouldLogin::class)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Token<*>? {
        val auth = webRequest.getHeader(HttpHeader.AUTHORIZATION.name)
            ?: if (shouldLogin(parameter))
                throw ErrorCode.NO_PERMISSION.error
            else return null
        return auth.token() ?: if (parameter.isOptional) null else throw ErrorCode.BAD_TOKEN.error
    }
}