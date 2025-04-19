package com.yjl.hnas.token

import com.yjl.hnas.entity.Uid
import java.time.Duration

/**
 * @author YJL
 */
object Auth {

    /**
     * 登录，默认7天
     */
    fun login(uid: Uid) = Token(uid).apply {
        register(Duration.ofDays(7))
    }

    /**
     * 登出
     */
    fun logout(token: Token) {
        token.unregister()
    }
}
