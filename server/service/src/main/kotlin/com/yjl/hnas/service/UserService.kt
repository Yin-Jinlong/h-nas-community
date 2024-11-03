package com.yjl.hnas.service

import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.entity.User
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.token.Token

/**
 * @author YJL
 */
interface UserService {

    fun genPassword(password: String): String

    fun isLogin(token: Token<UserInfo>): Boolean

    /**
     *
     * @param password 原始密码
     */
    fun login(uid: Uid, password: String): Token<UserInfo>

    /**
     *
     * @param password 原始密码
     */
    fun login(username: String, password: String): Token<UserInfo>

    fun logout(token: Token<UserInfo>)

    /**
     *
     * @param password 原始密码
     */
    fun register(username: String, password: String): User

}