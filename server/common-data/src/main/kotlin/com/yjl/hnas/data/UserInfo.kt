package com.yjl.hnas.data

import com.yjl.hnas.entity.User

/**
 * 用户信息
 *
 * @property uid 用户ID
 * @property username 用户名
 * @property nick 昵称
 * @author YJL
 */
data class UserInfo(
    val uid: Long,
    val username: String,
    val nick: String,
) {
    companion object {
        fun of(user: User): UserInfo {
            return UserInfo(user.uid, user.username, user.nick)
        }
    }

}