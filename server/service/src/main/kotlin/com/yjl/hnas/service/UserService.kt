package com.yjl.hnas.service

import com.yjl.hnas.entity.User
import com.yjl.hnas.entity.Uid

/**
 * @author YJL
 */
interface UserService {

    fun genPassword(password: String): String

    fun isLogin(uid: Uid): Boolean

    /**
     * 根据id获取已登录用户信息
     */
    fun getById(id: Uid): User?

    /**
     *
     * @param password 原始密码
     */
    fun login(uid: Uid, password: String): User

    /**
     *
     * @param password 原始密码
     */
    fun login(username: String, password: String): User

    fun logout(uid: Uid)

    /**
     *
     * @param password 原始密码
     */
    fun register(username: String, password: String): User

}