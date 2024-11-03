package com.yjl.hnas.controller

import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.service.UserService
import com.yjl.hnas.token.Token
import com.yjl.hnas.validator.Password
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotBlank
import org.eclipse.jetty.http.HttpHeader
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author YJL
 */
@RestController
class UserController(
    val userService: UserService
) {

    fun login(logId: String?, password: String?): Token<UserInfo> {
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

    @PostMapping("/api/user/login")
    fun login(
        token: Token<UserInfo>?,
        logId: String?,
        @Password password: String?,
        resp: HttpServletResponse
    ): UserInfo {
        return (token ?: login(logId, password)).let {
            resp.addHeader(HttpHeader.AUTHORIZATION.name, it.token)
            it.data
        }
    }

    @PostMapping("/api/user/logon")
    fun register(@NotBlank(message = "用户名不能为空") username: String, @Password password: String) {
        userService.register(username, password)
    }

}