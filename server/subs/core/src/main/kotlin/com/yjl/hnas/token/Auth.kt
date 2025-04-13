package com.yjl.hnas.token

import com.yjl.hnas.entity.Uid
import java.time.Duration

object Auth {

    fun login(uid: Uid) = Token(uid).apply {
        register(Duration.ofDays(7))
    }

    fun logout(token: Token) {
        token.unregister()
    }
}
