package com.yjl.hnas.token

import com.yjl.hnas.entity.Uid

/**
 * @author YJL
 */
data class UserTokenData(
    val uid: Uid,
    val type: TokenType
)
