package io.github.yinjinlong.hnas.controller

import io.github.yinjinlong.hnas.annotation.ShouldLogin
import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.error.ErrorCode
import io.github.yinjinlong.hnas.service.UserService
import io.github.yinjinlong.hnas.token.Token
import io.github.yinjinlong.hnas.utils.logger
import io.github.yinjinlong.spring.boot.annotations.ContentType
import io.github.yinjinlong.spring.boot.annotations.SkipHandle
import jakarta.servlet.ServletInputStream
import org.springframework.web.bind.annotation.*
import java.io.IOException
import javax.imageio.ImageIO

/**
 * @author YJL
 */
@RequestMapping(API.FILE)
@RestController
class UserController(
    val userService: UserService,
) {

    val logger = UserController::class.logger()

    @SkipHandle
    @ContentType("image/jpeg")
    @GetMapping("user/avatar")
    fun getUserAvatar(
        @RequestParam uid: Uid,
        @RequestParam(required = false) raw: Boolean = false,
    ): ByteArray {
        logger.info("get user avatar, uid: $uid")
        return userService.getAvatar(uid, raw)?.readBytes() ?: byteArrayOf()
    }

    @PostMapping("user/avatar")
    fun setUserAvatar(
        @ShouldLogin
        token: Token,
        instream: ServletInputStream
    ) {
        logger.info("set user avatar, uid: ${token.user}")
        try {
            val image = ImageIO.read(instream) ?: throw ErrorCode.BAD_REQUEST.error
            return userService.setAvatar(token.user, image)
        } catch (e: IOException) {
            throw ErrorCode.BAD_REQUEST.error
        }
    }

    @DeleteMapping("user/avatar")
    fun deleteUserAvatar(
        @ShouldLogin
        token: Token,
    ) {
        logger.info("delete user avatar, uid: ${token.user}")
        return userService.deleteAvatar(token.user)
    }

}