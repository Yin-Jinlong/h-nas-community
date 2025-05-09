package io.github.yinjinlong.hnas.service.impl

import com.google.gson.Gson
import io.github.yinjinlong.hnas.data.LoginQRInfo
import io.github.yinjinlong.hnas.data.LoginQRInfoStatus
import io.github.yinjinlong.hnas.data.LoginQRResult
import io.github.yinjinlong.hnas.data.UserInfo
import io.github.yinjinlong.hnas.entity.IUser
import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.entity.User
import io.github.yinjinlong.hnas.error.ErrorCode
import io.github.yinjinlong.hnas.mapper.UserMapper
import io.github.yinjinlong.hnas.service.UserService
import io.github.yinjinlong.hnas.token.Auth
import io.github.yinjinlong.hnas.token.Token
import io.github.yinjinlong.hnas.utils.base64Url
import io.github.yinjinlong.md.sha256
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.InetAddress
import java.time.Duration
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random

/**
 * @author YJL
 */
@Service
class UserServiceImpl(
    val mapper: UserMapper,
    val redis: StringRedisTemplate,
    val gson: Gson,
) : UserService {

    fun genQRID() = Random.nextBytes(12).base64Url

    fun qrIdKey(id: String) = "qr_id:$id"

    @OptIn(ExperimentalEncodingApi::class)
    override fun genPassword(password: String): String {
        return Base64.Default.encode(password.sha256)
    }

    override fun login(uid: Uid, password: String): UserService.LogResult {
        return (mapper.selectByUidPassword(uid, genPassword(password))?.let {
            UserService.LogResult(
                UserInfo.of(it),
                Auth.login(it.uid, it.role)
            )
        } ?: throw ErrorCode.USER_LOGIN_ERROR.data(uid))
    }

    override fun login(username: String, password: String): UserService.LogResult {
        return (mapper.selectByUsernamePassword(username, genPassword(password))?.let {
            UserService.LogResult(
                UserInfo.of(it),
                Auth.login(it.uid, it.role)
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

    override fun genQRLoginRequestID(sessionID: String, ip: InetAddress): String {
        return genQRID().also {
            setInfo(
                it,
                LoginQRInfo(LoginQRInfoStatus.WAITING, user = null, id = sessionID, ip = ip),
                Duration.ofMinutes(2)
            )
        }
    }

    override fun loginQR(sessionID: String, id: String): LoginQRResult {
        val info = info(id)
        return if (info == null)
            LoginQRResult(LoginQRInfoStatus.INVALID)
        else if (info.id != sessionID)
            throw ErrorCode.BAD_REQUEST.error
        else
            LoginQRResult(info.status, info.user, token = info.token?.token)
    }

    override fun getLoginQRInfo(user: Uid, id: String): LoginQRInfo? {
        return info(id)?.let { info ->
            if (info.status == LoginQRInfoStatus.WAITING && info.scannedUser == null) {
                val u = mapper.selectByUid(user) ?: throw ErrorCode.NO_SUCH_USER.error
                val token = Auth.login(u.uid, u.role)
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

    override fun getUser(uid: Uid): UserInfo? {
        return mapper.selectByUid(uid)?.let { UserInfo.of(it) }
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

    override fun setPassword(
        uid: Uid,
        oldPassword: String,
        newPassword: String
    ) {
        val user = mapper.selectByUidPassword(uid, genPassword(oldPassword)) ?: throw ErrorCode.BAD_REQUEST.error
        user.password = genPassword(newPassword)
        mapper.updatePasswordByUid(user)
        Auth.logoutAll(user.uid)
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

    @Transactional
    override fun setNick(uid: Uid, nick: String) {
        val user = User(uid = uid, nick = nick)
        mapper.updateNickNameByUid(user)
    }
}
