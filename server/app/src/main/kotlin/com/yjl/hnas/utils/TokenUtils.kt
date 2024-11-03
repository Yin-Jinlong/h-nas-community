package com.yjl.hnas.utils

import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.token.Token

fun String.token(): Token<*>? {
    try {
        val token = Token.from(this, UserInfo::class)
        if (token.isAvailable())
            return token
    } catch (_: Exception) {
    }
    return null
}