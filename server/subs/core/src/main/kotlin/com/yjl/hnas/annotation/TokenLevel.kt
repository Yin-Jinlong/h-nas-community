package com.yjl.hnas.annotation

import com.yjl.hnas.token.TokenType

/**
 * @author YJL
 */
@Target(AnnotationTarget.FUNCTION)
annotation class TokenLevel(
    val min: TokenType,
    val max: TokenType = TokenType.FULL_ACCESS
)
