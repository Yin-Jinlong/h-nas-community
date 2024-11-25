package com.yjl.hnas.service.impl

import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.IUser
import com.yjl.hnas.entity.User
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.mapper.UserMapper
import com.yjl.hnas.service.UserService
import com.yjl.hnas.token.Token
import com.yjl.hnas.token.TokenType
import com.yjl.hnas.token.UserTokenData
import com.yjl.hnas.utils.UserToken
import io.github.yinjinlong.md.sha256
import org.jetbrains.annotations.Range
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Calendar
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
        return Base64.Default.encode(password.sha256)
    }

    fun genToken(
        u: UserInfo,
        type: TokenType,
        filedId: Int,
        amount: Int
    ): UserToken {
        val time = Calendar.getInstance()
        time.add(filedId, amount)
        return Token.gen(UserTokenData(u, type), time)
    }

    fun genBaseToken(u: IUser): UserToken {
        return genToken(UserInfo.of(u), TokenType.AUTH, Calendar.DAY_OF_MONTH, 7)
    }

    override fun login(uid: Uid, password: String): UserToken {
        return (mapper.selectByUidPassword(uid, genPassword(password))?.let {
            genBaseToken(it)
        } ?: throw ErrorCode.USER_LOGIN_ERROR.data(uid))
    }

    override fun login(username: String, password: String): UserToken {
        return (mapper.selectByUsernamePassword(username, genPassword(password))?.let {
            genBaseToken(it)
        } ?: throw ErrorCode.USER_LOGIN_ERROR.data(username))
    }

    override fun genToken(token: UserToken, type: TokenType): UserToken {
        if (token.data.type == type)
            return token
        return when (type) {
            TokenType.AUTH -> token
            TokenType.FULL_ACCESS -> genToken(token.data.info, type, Calendar.MINUTE, 10)
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
            0,
            username,
            username,
            genPassword(password),
            IUser.PasswordType.SHA256
        ).apply {
            mapper.insert(this)
        }
    }
}
