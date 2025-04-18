package com.yjl.hnas.data

/**
 *
 * @author YJL
 */
data class LoginQRResult(
    val state: String,
    val user: UserInfo? = null,
    val token: String? = null,
)
