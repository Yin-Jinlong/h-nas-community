package com.yjl.hnas.entity

import com.yjl.hnas.entity.IUser.PasswordType
import io.github.yinjinlong.spring.boot.annotations.JsonIgnored
import jakarta.persistence.*
import org.hibernate.annotations.Comment

/**
 * @author YJL
 */
@Entity
@Table
@Comment("用户")
data class User(

    @Id
    @Column(columnDefinition = "int8 default (UUID_SHORT() % 1000000000000)")
    override var uid: Uid = 0,

    @Column(length = 64, nullable = false, unique = true)
    @Comment("用户名")
    override var username: String = "",

    @Column(length = 64, nullable = false)
    @Comment("昵称")
    override var nick: String = "",

    @Column(length = 128, nullable = false)
    @Comment("密码")
    @field:JsonIgnored
    override var password: String = "",

    @Enumerated(EnumType.STRING)
    @Column(length = 24, nullable = false)
    @Comment("密码类型")
    @field:JsonIgnored
    override var passwordType: PasswordType = PasswordType.SHA256
) : IUser