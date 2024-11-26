package com.yjl.hnas.ar

import com.yjl.hnas.annotation.ShouldLogin
import com.yjl.hnas.annotation.TokenLevel
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.token.Token
import com.yjl.hnas.token.UserTokenData
import com.yjl.hnas.utils.UserToken
import com.yjl.hnas.utils.hasAnno
import com.yjl.hnas.utils.token
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import java.lang.reflect.ParameterizedType

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
        val auth = webRequest.getHeader(HttpHeaders.AUTHORIZATION)
            ?: if (shouldLogin(parameter))
                throw ErrorCode.BAD_TOKEN.error
            else return null

        val t = parameter.parameter.parameterizedType as ParameterizedType
        val p0 = t.actualTypeArguments[0] as Class<*>
        val r = auth.token(p0.kotlin)
            ?: return if (parameter.isOptional) null else throw ErrorCode.BAD_TOKEN.error

        if (UserTokenData::class.java.isAssignableFrom(p0)) {
            val token = r as UserToken
            val tl = parameter.method!!.getAnnotation(TokenLevel::class.java)
            if (tl != null) {
                if (token.data.type.level < tl.min.level || token.data.type.level > tl.max.level)
                    throw ErrorCode.BAD_TOKEN.error
            }
        }

        return r
    }
}