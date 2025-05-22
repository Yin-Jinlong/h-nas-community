package io.github.yinjinlong.hnas.service

import io.github.yinjinlong.hnas.data.LoginQRInfo
import io.github.yinjinlong.hnas.data.LoginQRResult
import io.github.yinjinlong.hnas.data.UserInfo
import io.github.yinjinlong.hnas.entity.IUser
import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.token.Token
import java.net.InetAddress

/**
 * @author YJL
 */
interface UserService : IService {

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

    fun genQRLoginRequestID(sessionID: String, ip: InetAddress): String

    fun loginQR(sessionID: String, id: String): LoginQRResult

    fun getLoginQRInfo(user: Uid, id: String): LoginQRInfo?

    fun grant(id: String)

    fun getUserCount(uid: Uid): Int

    fun getUser(uid: Uid): UserInfo?

    fun getUsers(user: Uid, startId: Uid, count: Int): List<UserInfo>

    fun cancelRequest(id: String)

    fun logout(token: Token)

    fun setPassword(uid: Uid, oldPassword: String, newPassword: String)

    /**
     *
     * @param password 原始密码
     */
    fun register(username: String, password: String): IUser

    fun setNick(uid: Uid, nick: String)

    data class LogResult(
        val user: UserInfo,
        val token: Token
    )
}