package com.yjl.hnas.data

/**
 *
 * @author YJL
 */
data class LoginQRResult(
    val status: LoginQRInfoStatus,
    val user: UserInfo? = null,
    val token: String? = null,
)
