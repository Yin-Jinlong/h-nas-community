package io.github.yinjinlong.hnas.service

import io.github.yinjinlong.hnas.entity.Uid
import java.awt.image.BufferedImage
import java.io.File

/**
 * @author YJL
 */
interface UserService {

    fun getAvatar(uid: Uid,raw:  Boolean): File?

    fun setAvatar(uid: Uid, image: BufferedImage)

}