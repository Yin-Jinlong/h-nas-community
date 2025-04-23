package io.github.yinjinlong.hnas.ar

import io.github.yinjinlong.hnas.annotation.ShouldLogin
import io.github.yinjinlong.hnas.error.ErrorCode
import io.github.yinjinlong.hnas.token.Token
import io.github.yinjinlong.hnas.utils.hasAnno
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * [Token]参数解析
 *
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
    ): Token? {
        val auth = webRequest.getHeader(HttpHeaders.AUTHORIZATION)
            ?: if (shouldLogin(parameter))
                throw ErrorCode.BAD_TOKEN.error
            else return null

        return Token[auth]
    }
}