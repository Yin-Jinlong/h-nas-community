package com.yjl.hnas.controller

import com.yjl.hnas.entity.User
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.service.UserService
import com.yjl.hnas.validator.Password
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author YJL
 */
@RestController
class UserController(
    val userService: UserService
) {

    @PostMapping("/api/user/login")
    fun login(logId: String, @Password password: String?): User {
        if (logId.isBlank())
            throw ErrorCode.BAD_ARGUMENTS.data("logId")
        if (password == null) {
            return userService.getById(
                logId.toLongOrNull()
                    ?: throw ErrorCode.BAD_ARGUMENTS.data("logId")
            )
                ?: throw ErrorCode.USER_NOT_LOGIN.error
        }
        if (password.isBlank()) {
            throw ErrorCode.BAD_ARGUMENTS.data("password")
        }
        val uid = logId.toLongOrNull()
        return if (uid != null)
            userService.login(uid, password)
        else
            userService.login(logId, password)
    }

    @PostMapping("/api/user/logon")
    fun register(@NotBlank(message = "用户名不能为空") username: String, @Password password: String) {
        userService.register(username, password)
    }

}