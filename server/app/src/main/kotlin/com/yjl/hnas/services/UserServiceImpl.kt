package com.yjl.hnas.services

import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.User
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.mapper.UserMapper
import com.yjl.hnas.service.UserService
import com.yjl.hnas.token.Token
import io.github.yinjinlong.md.sha256
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * @author YJL
 */
@Service
class UserServiceImpl(
    val mapper: UserMapper
) : UserService {

    @OptIn(ExperimentalEncodingApi::class)
    override fun genPassword(password: String): String {
        return Base64.encode(password.sha256)
    }

    override fun isLogin(token: Token<UserInfo>): Boolean {
        return token.isAvailable()
    }

    fun genToken(u: User): Token<UserInfo> {
        val time = Calendar.getInstance()
        time.add(Calendar.MINUTE, 30)
        return Token.gen(UserInfo.of(u), time)
    }

    override fun login(uid: Uid, password: String): Token<UserInfo> {
        return (mapper.selectByUidPassword(uid, genPassword(password))?.let {
            genToken(it)
        } ?: throw ErrorCode.USER_LOGIN_ERROR.data(uid))
    }

    override fun login(username: String, password: String): Token<UserInfo> {
        return (mapper.selectByUsernamePassword(username, genPassword(password))?.let {
            genToken(it)
        } ?: throw ErrorCode.USER_LOGIN_ERROR.data(username))
    }

    override fun logout(token: Token<UserInfo>) {

    }

    @Transactional
    override fun register(username: String, password: String): User {
        mapper.selectByUsername(username)?.let {
            throw ErrorCode.USER_EXISTS.data(username)
        }
        return User(
            0,
            username,
            username,
            genPassword(password),
            User.PasswordType.SHA256
        ).apply {
            mapper.insert(this)
        }
    }
}