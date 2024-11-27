package com.yjl.hnas.entity

typealias Uid = Long

/**
 * @author YJL
 */
interface IUser {

    var uid: Uid

    var username: String

    var nick: String

    var avatar: String?

    var password: String

    var passwordType: PasswordType

    enum class PasswordType {
        MD5,
        SHA256,
        SHA512
    }
}
