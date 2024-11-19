package com.yjl.hnas.utils

import com.yjl.hnas.token.Token
import kotlin.reflect.KClass

fun <T : Any> String.token(type: KClass<T>): Token<T>? {
    try {
        val token = Token.from(this, type)
        if (token.isAvailable())
            return token
    } catch (_: Exception) {
    }
    return null
}
