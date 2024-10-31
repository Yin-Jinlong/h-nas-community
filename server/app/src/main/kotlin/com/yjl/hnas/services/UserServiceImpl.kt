package com.yjl.hnas.services

import com.yjl.hnas.entity.User
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.mapper.UserMapper
import com.yjl.hnas.service.UserService
import io.github.yinjinlong.md.sha256
import org.springframework.stereotype.Service
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * @author YJL
 */
@Service
class UserServiceImpl(
    val mapper: UserMapper
) : UserService {

    val loginUser = HashSet<Uid>()

    @OptIn(ExperimentalEncodingApi::class)
    override fun genPassword(password: String): String {
        return Base64.encode(password.sha256)
    }

    override fun isLogin(uid: Uid): Boolean {
        return loginUser.contains(uid)
    }

    override fun getById(id: Uid): User? {
        return if (isLogin(id))
            mapper.selectByUid(id)
        else null
    }

    override fun login(uid: Uid, password: String): User {
        return (mapper.selectByUidPassword(uid, genPassword(password))?.apply {
            loginUser.add(uid)
        } ?: throw ErrorCode.USER_LOGIN_ERROR.data(uid))
    }

    override fun login(username: String, password: String): User {
        return (mapper.selectByUsernamePassword(username, genPassword(password))?.apply {
            loginUser.add(uid)
        } ?: throw ErrorCode.USER_LOGIN_ERROR.data(username))
    }

    override fun logout(uid: Uid) {
        loginUser.remove(uid)
    }

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