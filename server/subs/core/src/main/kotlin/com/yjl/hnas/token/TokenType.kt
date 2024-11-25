package com.yjl.hnas.token

/**
 * @author YJL
 */
enum class TokenType(val level: Int) {
    /**
     * 仅用于认证，生成更高权限token
     */
    AUTH(1),

    /**
     * 所有权限
     */
    FULL_ACCESS(999)
}
