package io.github.yinjinlong.hnas.controller

import io.github.yinjinlong.hnas.annotation.ShouldLogin
import io.github.yinjinlong.hnas.data.LoginQRInfoStatus
import io.github.yinjinlong.hnas.data.LoginQRResult
import io.github.yinjinlong.hnas.data.QRGrantInfo
import io.github.yinjinlong.hnas.data.UserInfo
import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.error.ErrorCode
import io.github.yinjinlong.hnas.service.UserService
import io.github.yinjinlong.hnas.token.Token
import io.github.yinjinlong.hnas.utils.logger
import io.github.yinjinlong.hnas.validator.Password
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Range
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import java.net.InetAddress

/**
 * @author YJL
 */
@RestController
@RequestMapping(API.USER)
class UserController(
    val userService: UserService
) {
    val logger = UserController::class.logger()

    fun login(logId: String?, password: String?): UserService.LogResult {
        if (logId.isNullOrBlank())
            throw ErrorCode.BAD_ARGUMENTS.data("logId")
        if (password.isNullOrBlank()) {
            throw ErrorCode.BAD_ARGUMENTS.data("password")
        }
        val uid = logId.toLongOrNull()
        return if (uid != null)
            userService.login(uid, password)
        else
            userService.login(logId, password)
    }

    @PostMapping("login")
    fun login(
        logId: String?,
        @Password password: String?,
        resp: HttpServletResponse
    ): UserInfo {
        logger.info("login $logId => '$password'")
        return login(logId, password).let {
            resp.addHeader(HttpHeaders.AUTHORIZATION, it.token.token)
            it.user
        }
    }

    @PostMapping("login/qr/request")
    @Suppress("UastIncorrectHttpHeaderInspection")
    fun requestLoginQR(
        session: HttpSession,
        @RequestHeader("X-Real-IP") ip: String
    ): String {
        logger.info("requestLoginQR ${session.id} $ip")
        return userService.genQRLoginRequestID(
            session.id, InetAddress.getByName(
                ip
            )
        )
    }

    @PostMapping("login/qr")
    fun loginQR(
        @RequestParam id: String,
        session: HttpSession
    ): LoginQRResult {
        logger.info("loginQR ${session.id} $id")
        return userService.loginQR(session.id, id)
    }

    @PostMapping("grant/qr/info")
    fun getLoginQRInfo(
        @ShouldLogin token: Token,
        @RequestParam id: String,
    ): QRGrantInfo {
        logger.info("getLoginQRInfo ${token.user} $id")
        return userService.getLoginQRInfo(token.user, id)?.let {
            if (it.scannedUser != token.user)
                null
            else
                QRGrantInfo(it.ip.hostAddress)
        } ?: throw ErrorCode.BAD_REQUEST.error
    }

    @GetMapping("info")
    fun getUserInfo(
        @ShouldLogin token: Token
    ): UserInfo {
        logger.info("getUserInfo ${token.user}")
        return userService.getUser(token.user) ?: throw ErrorCode.BAD_ARGUMENTS.error
    }

    @PostMapping("grant/qr")
    fun grantQRLogin(
        @ShouldLogin token: Token,
        @RequestParam id: String,
        @RequestParam grant: Boolean
    ) {
        logger.info("grantQRLogin ${token.user} $id $grant")
        val info = userService.getLoginQRInfo(token.user, id)
        if (info?.status != LoginQRInfoStatus.SCANNED || info.scannedUser != token.user)
            throw ErrorCode.BAD_REQUEST.error
        if (grant)
            userService.grant(id)
        else
            userService.cancelRequest(id)
    }

    @GetMapping("count")
    fun getUserCount(
        @ShouldLogin token: Token
    ): Int {
        logger.info("getUserCount ${token.user}")
        return userService.getUserCount(token.user)
    }

    @GetMapping("users")
    fun getUsers(
        @ShouldLogin token: Token,
        @RequestParam startId: Uid,
        @RequestParam
        @Range(min = 1, max = 100)
        count: Int
    ): List<UserInfo> {
        logger.info("getUsers ${token.user} $startId $count")
        return userService.getUsers(token.user, startId, count)
    }

    @PostMapping("logon")
    fun register(@NotBlank(message = "用户名不能为空") username: String, @Password password: String) {
        logger.info("register $username => $password")
        if (username.matches("\\d+".toRegex()))
            throw ErrorCode.BAD_ARGUMENTS.data("username")
        userService.register(username, password)
    }

    @PostMapping("password")
    fun changePassword(
        @ShouldLogin token: Token,
        @RequestParam("old")
        @Password
        oldPassword: String,
        @RequestParam("new")
        @Password
        newPassword: String,
    ) {
        userService.setPassword(token.user, oldPassword, newPassword)
    }

    @PatchMapping("nick")
    fun setNick(
        @ShouldLogin token: Token,
        @RequestParam
        @NotBlank
        nick: String
    ) {
        logger.info("setNick ${token.user} $nick")
        userService.setNick(token.user, nick)
    }
}
