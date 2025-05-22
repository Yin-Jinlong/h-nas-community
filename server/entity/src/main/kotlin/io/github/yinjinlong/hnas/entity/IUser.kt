package io.github.yinjinlong.hnas.entity

typealias Uid = Long

/**
 * @author YJL
 */
interface IUser {

    var uid: Uid

    var username: String

    var nick: String

    var password: String

    var passwordType: PasswordType

    var role: String

    enum class PasswordType {
        MD5,
        SHA256,
        SHA512
    }

    companion object {
        const val TABLE = "user"

        const val ROLE_USER = "user"
        const val ROLE_ADMIN = "admin"
    }
}
