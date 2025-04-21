package com.yjl.hnas.data

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.token.Token
import java.net.InetAddress

/**
 *
 * @author YJL
 */
data class LoginQRInfo(
    val status: LoginQRInfoStatus,
    val user: UserInfo?,
    val ip: InetAddress,
    val scannedUser: Uid? = null,
    val token: Token? = null,
)