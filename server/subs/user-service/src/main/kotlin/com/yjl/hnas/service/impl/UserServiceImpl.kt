package com.yjl.hnas.service.impl

import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.entity.IUser
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.User
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.mapper.UserMapper
import com.yjl.hnas.service.UserService
import com.yjl.hnas.token.Token
import com.yjl.hnas.token.TokenType
import com.yjl.hnas.token.UserTokenData
import com.yjl.hnas.utils.UserToken
import io.github.yinjinlong.md.sha256
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * @author YJL
 */
@Service
class UserServiceImpl(
    val mapper: UserMapper,
    val stringRedisTemplate: StringRedisTemplate
) : UserService {

    @OptIn(ExperimentalEncodingApi::class)
    override fun genPassword(password: String): String {
        return Base64.Default.encode(password.sha256)
    }

    fun genToken(
        uid: Uid,
        type: TokenType,
        filedId: Int,
        amount: Int
    ): UserToken {
        val time = Calendar.getInstance()
        time.add(filedId, amount)
        return Token.gen(UserTokenData(uid, type), time)
    }

    fun genBaseToken(u: IUser): UserToken {
        val k = "auth_token_${u.uid}"
        val v = stringRedisTemplate.opsForValue().get(k)
        if (v != null) {
            return Token.from(v, UserTokenData::class)
        }
        return genToken(u.uid, TokenType.AUTH, Calendar.DAY_OF_MONTH, 7).also {
            stringRedisTemplate.opsForValue().set(
                k,
                it.token,
                7,
                TimeUnit.DAYS
            )
        }
    }

    override fun login(token: UserToken): UserService.LogResult {
        val u = mapper.selectByUid(token.data.uid)
            ?: throw ErrorCode.BAD_TOKEN.error
        return UserService.LogResult(
            UserInfo.of(u),
            token
        )
    }

    override fun login(uid: Uid, password: String): UserService.LogResult {
        return (mapper.selectByUidPassword(uid, genPassword(password))?.let {
            UserService.LogResult(
                UserInfo.of(it),
                genBaseToken(it)
            )
        } ?: throw ErrorCode.USER_LOGIN_ERROR.data(uid))
    }

    override fun login(username: String, password: String): UserService.LogResult {
        return (mapper.selectByUsernamePassword(username, genPassword(password))?.let {
            UserService.LogResult(
                UserInfo.of(it),
                genBaseToken(it)
            )
        } ?: throw ErrorCode.USER_LOGIN_ERROR.data(username))
    }

    override fun genToken(token: UserToken, type: TokenType): UserToken {
        if (token.data.type == type)
            return token
        return when (type) {
            TokenType.AUTH -> token
            TokenType.FULL_ACCESS -> genToken(token.data.uid, type, Calendar.MINUTE, 10)
        }
    }

    override fun logout(token: UserToken) {

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
