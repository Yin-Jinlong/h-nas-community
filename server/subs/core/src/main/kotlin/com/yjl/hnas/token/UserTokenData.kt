package com.yjl.hnas.token

import com.yjl.hnas.data.UserInfo

/**
 * @author YJL
 */
data class UserTokenData(
    val info: UserInfo,
    val type: TokenType
)
