package com.yjl.hnas.entity

import io.github.yinjinlong.spring.boot.annotations.JsonIgnored
import jakarta.persistence.*
import org.hibernate.annotations.Comment

typealias Uid = Long

/**
 * @author YJL
 */
@Entity
@Table
@Comment("用户")
data class User(

    @Id
    @Column(columnDefinition = "int8 default (UUID_SHORT() % 1000000000000)")
    var uid: Uid = 0,

    @Column(length = 64, nullable = false, unique = true)
    @Comment("用户名")
    var username: String = "",

    @Column(length = 64, nullable = false)
    @Comment("昵称")
    var nick: String = "",

    @Column(length = 128, nullable = false)
    @Comment("密码")
    @field:JsonIgnored
    var password: String = "",

    @Enumerated(EnumType.STRING)
    @Column(length = 24, nullable = false)
    @Comment("密码类型")
    @field:JsonIgnored
    var passwordType: PasswordType = PasswordType.SHA256
) {
    enum class PasswordType {
        MD5,
        SHA256,
        SHA512
    }
}