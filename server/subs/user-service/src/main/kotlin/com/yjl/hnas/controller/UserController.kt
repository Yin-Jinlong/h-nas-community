package com.yjl.hnas.controller

import com.yjl.hnas.annotation.ShouldLogin
import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.service.UserService
import com.yjl.hnas.token.TokenType
import com.yjl.hnas.utils.UserToken
import com.yjl.hnas.validator.Password
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author YJL
 */
@RestController
@RequestMapping("/api/user")
class UserController(
    val userService: UserService
) {

    fun login(logId: String?, password: String?): UserToken {
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
        token: UserToken?,
        logId: String?,
        @Password password: String?,
        resp: HttpServletResponse
    ): UserInfo {
        return (token ?: login(logId, password)).let {
            resp.addHeader(HttpHeaders.AUTHORIZATION, it.token)
            it.data.info
        }
    }

    @PostMapping("auth")
    fun auth(
        @ShouldLogin token: UserToken,
        resp: HttpServletResponse
    ) {
        val nt = userService.genToken(token, TokenType.FULL_ACCESS)
        resp.addHeader(HttpHeaders.AUTHORIZATION, nt.token)
    }

    @PostMapping("logon")
    fun register(@NotBlank(message = "用户名不能为空") username: String, @Password password: String) {
        userService.register(username, password)
    }

}