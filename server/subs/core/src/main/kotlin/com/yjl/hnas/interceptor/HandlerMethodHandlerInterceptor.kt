package com.yjl.hnas.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.lang.Exception

/**
 * @author YJL
 */
open class HandlerMethodHandlerInterceptor : HandlerInterceptor {

    open fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: HandlerMethod
    ): Boolean = true

    open fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: HandlerMethod,
        modelAndView: ModelAndView?
    ) = Unit


    open fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: HandlerMethod,
        ex: Exception?
    ) = Unit

    final override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod)
            return preHandle(request, response, handler)
        return true
    }

    final override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        if (handler is HandlerMethod)
            afterCompletion(request, response, handler, ex)
    }

    final override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        if (handler is HandlerMethod)
            postHandle(request, response, handler, modelAndView)
    }
}