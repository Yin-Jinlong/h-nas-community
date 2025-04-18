package com.yjl.hnas.service

import com.yjl.hnas.data.LoginQRInfo
import com.yjl.hnas.data.LoginQRResult
import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.entity.IUser
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.token.Token
import java.net.InetAddress

/**
 * @author YJL
 */
interface UserService {

    /**
     *
     * @param password 原始密码
     */
    fun genPassword(password: String): String

    /**
     *
     * @param password 原始密码
     */
    fun login(uid: Uid, password: String): LogResult

    /**
     *
     * @param password 原始密码
     */
    fun login(username: String, password: String): LogResult

    fun genQRLoginRequestID(id: String, ip: InetAddress): String

    fun loginQR(id: String): LoginQRResult

    fun getLoginQRInfo(user: Uid, id: String): LoginQRInfo?

    fun grant(id: String)

    fun cancelRequest(id: String)

    fun logout(token: Token)

    /**
     *
     * @param password 原始密码
     */
    fun register(username: String, password: String): IUser

    data class LogResult(
        val user: UserInfo,
        val token: Token
    )
}