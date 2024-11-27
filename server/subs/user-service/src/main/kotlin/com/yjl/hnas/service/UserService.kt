package com.yjl.hnas.service

import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.entity.IUser
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.token.TokenType
import com.yjl.hnas.utils.UserToken

/**
 * @author YJL
 */
interface UserService {

    /**
     *
     * @param password 原始密码
     */
    fun genPassword(password: String): String

    fun login(token: UserToken): LogResult

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

    /**
     * 生成其他类型token
     */
    fun genToken(token: UserToken, type: TokenType): UserToken

    fun logout(token: UserToken)

    /**
     *
     * @param password 原始密码
     */
    fun register(username: String, password: String): IUser

    data class LogResult(
        val user: UserInfo,
        val token: UserToken
    )
}