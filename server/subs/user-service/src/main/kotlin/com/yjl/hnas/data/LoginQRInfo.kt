package com.yjl.hnas.data

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.token.Token
import java.net.InetAddress

/**
 *
 * @author YJL
 */
data class LoginQRInfo(
    val status: Status,
    val user: UserInfo?,
    val ip: InetAddress,
    val scannedUser: Uid? = null,
    val token: Token? = null,
) {
    enum class Status {
        /**
         * 等待扫码
         */
        WAITING,

        /**
         * 已扫码
         */
        SCANNED,

        /**
         * 成功
         */
        SUCCESS,

        /**
         * 失败
         */
        FAILED,

        /**
         * 无效
         */
        INVALID,
    }
}