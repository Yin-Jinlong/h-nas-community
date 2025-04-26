package io.github.yinjinlong.hnas.token

import io.github.yinjinlong.hnas.entity.Uid
import java.time.Duration

/**
 * @author YJL
 */
object Auth {

    /**
     * 登录，默认7天
     */
    fun login(uid: Uid, role: String) = Token(uid, role).apply {
        register(Duration.ofDays(7))
    }

    /**
     * 登出
     */
    fun logout(token: Token) {
        token.unregister()
    }
}
