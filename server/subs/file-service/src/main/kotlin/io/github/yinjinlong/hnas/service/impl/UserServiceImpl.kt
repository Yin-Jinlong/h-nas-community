package io.github.yinjinlong.hnas.service.impl

import io.github.yinjinlong.hnas.data.DataHelper
import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.service.UserService
import io.github.yinjinlong.hnas.utils.mkParent
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

/**
 * @author YJL
 */
@Service
class UserServiceImpl : UserService {

    override fun getAvatar(uid: Uid): File? {
        return DataHelper.avatarFile(uid).let {
            if (it.exists()) it
            else null
        }
    }

    override fun setAvatar(uid: Uid, image: BufferedImage) {
        val size = minOf(400, image.width, image.height)
        val avatar = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        avatar.graphics.apply {
            drawImage(image.getCenterImage(), 0, 0, size, size, null)
        }
        val avatarFile = DataHelper.avatarFile(uid)
        ImageIO.getImageWritersBySuffix("png").next().apply {
            val out = ByteArrayOutputStream()
            output = ImageIO.createImageOutputStream(out)
            defaultWriteParam.apply {
                compressionMode = ImageWriteParam.MODE_EXPLICIT
                compressionQuality = 1f
            }
            write(avatar)
            avatarFile.mkParent()
            avatarFile.writeBytes(out.toByteArray())
        }
    }

    fun BufferedImage.getCenterImage(): BufferedImage {
        val size = minOf(width, height)
        return if (size == width) {
            getSubimage(0, (height - size) / 2, size, size)
        } else {
            getSubimage((width - size) / 2, 0, size, size)
        }
    }
}