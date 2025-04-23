package io.github.yinjinlong.hnas.data

import io.github.yinjinlong.hnas.entity.IUser

/**
 * 用户信息
 *
 * @property uid 用户ID
 * @property username 用户名
 * @property nick 昵称
 * @property avatar 头像
 * @author YJL
 */
data class UserInfo(
    val uid: Long,
    val username: String,
    val nick: String,
    val avatar: String?,
    val admin: Boolean,
) {
    companion object {
        fun of(user: IUser): UserInfo {
            return UserInfo(user.uid, user.username, user.nick, user.avatar, user.role == IUser.ROLE_ADMIN)
        }
    }

}