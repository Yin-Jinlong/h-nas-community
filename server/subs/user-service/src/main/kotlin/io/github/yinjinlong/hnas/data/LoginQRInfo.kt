package io.github.yinjinlong.hnas.data

import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.token.Token
import java.net.InetAddress

/**
 *
 * @author YJL
 */
data class LoginQRInfo(
    val status: LoginQRInfoStatus,
    val user: UserInfo?,
    val id: String,
    val ip: InetAddress,
    val scannedUser: Uid? = null,
    val token: Token? = null,
)