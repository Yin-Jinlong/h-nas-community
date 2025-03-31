package com.yjl.hnas.service.impl

import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.entity.IUser
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.User
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.mapper.UserMapper
import com.yjl.hnas.service.UserService
import com.yjl.hnas.token.Auth
import com.yjl.hnas.token.Token
import com.yjl.hnas.token.TokenType
import io.github.yinjinlong.md.sha256
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * @author YJL
 */
@Service
class UserServiceImpl(
    val mapper: UserMapper,
) : UserService {

    @OptIn(ExperimentalEncodingApi::class)
    override fun genPassword(password: String): String {
        return Base64.Default.encode(password.sha256)
    }

    override fun login(uid: Uid, password: String): UserService.LogResult {
        return (mapper.selectByUidPassword(uid, genPassword(password))?.let {
            UserService.LogResult(
                UserInfo.of(it),
                Auth.login(it.uid)
            )
        } ?: throw ErrorCode.USER_LOGIN_ERROR.data(uid))
    }

    override fun login(username: String, password: String): UserService.LogResult {
        return (mapper.selectByUsernamePassword(username, genPassword(password))?.let {
            UserService.LogResult(
                UserInfo.of(it),
                Auth.login(it.uid)
            )
        } ?: throw ErrorCode.USER_LOGIN_ERROR.data(username))
    }

    override fun genToken(token: Token, type: TokenType): Token {
        return when (type) {
            TokenType.FULL_ACCESS -> Auth.fullAccessToken(token.user)

            else -> throw ErrorCode.BAD_REQUEST.error
        }
    }

    override fun logout(token: Token) {
        Auth.logout(token)
    }

    @Transactional
    override fun register(username: String, password: String): IUser {
        mapper.selectByUsername(username)?.let {
            throw ErrorCode.USER_EXISTS.data(username)
        }
        return User(
            username = username,
            nick = username,
            password = genPassword(password),
            role = if (mapper.hasUser()) IUser.ROLE_USER else IUser.ROLE_ADMIN
        ).apply {
            mapper.insert(this)
        }
    }
}
