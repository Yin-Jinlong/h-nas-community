package com.yjl.hnas.token

import com.yjl.hnas.entity.Uid
import java.time.Duration

object Auth {

    fun login(uid: Uid) = Token(uid, TokenType.AUTH).apply {
        register(Duration.ofDays(7))
    }

    fun fullAccessToken(uid: Uid) = Token(uid, TokenType.FULL_ACCESS).apply {
        register(Duration.ofMinutes(10))
    }

    fun logout(token: Token) {
        token.unregister()
    }
}
