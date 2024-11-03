package com.yjl.hnas.data

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.User

/**
 * @author YJL
 */
data class UserInfo(
    val uid: Uid,
    val username: String,
    val nick: String,
) {
    companion object {
        fun of(user: User): UserInfo {
            return UserInfo(user.uid, user.username, user.nick)
        }
    }

}