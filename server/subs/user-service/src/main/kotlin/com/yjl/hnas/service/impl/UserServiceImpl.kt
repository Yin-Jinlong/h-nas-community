package com.yjl.hnas.service.impl

import com.google.gson.Gson
import com.yjl.hnas.data.LoginQRInfo
import com.yjl.hnas.data.LoginQRInfoStatus
import com.yjl.hnas.data.LoginQRResult
import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.entity.IUser
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.User
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.mapper.UserMapper
import com.yjl.hnas.service.UserService
import com.yjl.hnas.token.Auth
import com.yjl.hnas.token.Token
import io.github.yinjinlong.md.sha256
import io.github.yinjinlong.md.sha256Hex
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.InetAddress
import java.time.Duration
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * @author YJL
 */
@Service
class UserServiceImpl(
    val mapper: UserMapper,
    val redis: StringRedisTemplate,
    val gson: Gson,
) : UserService {

    fun qrIdKey(id: String) = "qr_id:$id"

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

    fun info(id: String): LoginQRInfo? = redis.opsForValue().get(qrIdKey(id))?.let {
        gson.fromJson(it, LoginQRInfo::class.java)
    }

    fun setInfo(id: String, info: LoginQRInfo, timeout: Duration = Duration.ofMinutes(1)) {
        redis.opsForValue().set(
            qrIdKey(id),
            gson.toJson(info),
            timeout
        )
    }

    override fun genQRLoginRequestID(id: String, ip: InetAddress): String {
        return id.sha256Hex.also {
            setInfo(
                it,
                LoginQRInfo(LoginQRInfoStatus.WAITING, user = null, ip = ip),
                Duration.ofMinutes(2)
            )
        }
    }

    override fun loginQR(id: String): LoginQRResult {
        val info = info(id)
        return if (info == null)
            LoginQRResult(LoginQRInfoStatus.INVALID)
        else
            LoginQRResult(info.status, info.user, token = info.token?.token)
    }

    override fun getLoginQRInfo(user: Uid, id: String): LoginQRInfo? {
        return info(id)?.let { info ->
            if (info.status == LoginQRInfoStatus.WAITING && info.scannedUser == null) {
                val token = Auth.login(user)
                info.copy(status = LoginQRInfoStatus.SCANNED, scannedUser = user, token = token).also {
                    setInfo(id, it)
                }
            } else info
        }
    }

    override fun grant(id: String) {
        info(id)?.let {
            setInfo(
                id,
                it.copy(
                    status = LoginQRInfoStatus.SUCCESS,
                    user = UserInfo.of(
                        it.scannedUser?.let { uid -> mapper.selectByUid(uid) }
                            ?: throw IllegalStateException("用户id不存在：${it.scannedUser}")
                    )
                )
            )
        }
    }

    fun checkAdmin(uid: Uid): User {
        val user = mapper.selectByUid(uid)
            ?: throw ErrorCode.NO_PERMISSION.error
        return if (user.role != IUser.ROLE_ADMIN)
            throw ErrorCode.NO_PERMISSION.error
        else
            user
    }

    override fun getUserCount(uid: Uid): Int {
        checkAdmin(uid)
        return mapper.selectUserCount()
    }

    override fun getUsers(user: Uid, startId: Uid, count: Int): List<UserInfo> {
        checkAdmin(user)
        return mapper.selectUsers(startId, count).map {
            UserInfo.of(it)
        }
    }

    override fun cancelRequest(id: String) {
        info(id)?.let {
            setInfo(
                id,
                it.copy(status = LoginQRInfoStatus.FAILED)
            )
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
