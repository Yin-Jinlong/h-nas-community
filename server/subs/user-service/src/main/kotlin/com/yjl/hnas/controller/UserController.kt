package com.yjl.hnas.controller

import com.yjl.hnas.annotation.ShouldLogin
import com.yjl.hnas.data.LoginQRInfo
import com.yjl.hnas.data.LoginQRResult
import com.yjl.hnas.data.QRGrantInfo
import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.service.UserService
import com.yjl.hnas.token.Token
import com.yjl.hnas.validator.Password
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
        return login(logId, password).let {
            resp.addHeader(HttpHeaders.AUTHORIZATION, it.token.token)
            it.user
        }
    }

    @PostMapping("login/qr/request")
    fun requestLoginQR(
        session: HttpSession,
        @RequestHeader("X-Real-IP") ip: String
    ): String {
        return userService.genQRLoginRequestID(
            session.id, InetAddress.getByName(
                ip
            )
        )
    }

    @PostMapping("login/qr")
    fun loginQR(
        @RequestParam id: String,
    ): LoginQRResult {
        return userService.loginQR(id)
    }

    @PostMapping("grant/qr/info")
    fun getLoginQRInfo(
        @ShouldLogin token: Token,
        @RequestParam id: String,
    ): QRGrantInfo {
        return userService.getLoginQRInfo(token.user, id)?.let {
            if (it.scannedUser != token.user)
                null
            else
                QRGrantInfo(it.ip.hostAddress)
        } ?: throw ErrorCode.BAD_REQUEST.error
    }

    @PostMapping("grant/qr")
    fun grantQRLogin(
        @ShouldLogin token: Token,
        @RequestParam id: String,
        @RequestParam grant: Boolean
    ) {
        val info = userService.getLoginQRInfo(token.user, id)
        if (info?.status != LoginQRInfo.Status.SCANNED)
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
        return userService.getUsers(token.user, startId, count)
    }

    @PostMapping("logon")
    fun register(@NotBlank(message = "用户名不能为空") username: String, @Password password: String) {
        userService.register(username, password)
    }

}