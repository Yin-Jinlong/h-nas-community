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

    override fun getAvatar(uid: Uid, raw: Boolean): File? {
        return (if (raw) DataHelper.avatarFile(uid) else DataHelper.avatarSmallFile(uid)).let {
            if (it.exists()) it
            else null
        }
    }

    fun genAvatar(image: BufferedImage, size: Int): BufferedImage {
        val imgSize = minOf(size, image.width, image.height)
        return BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB).apply {
            graphics.apply {
                drawImage(image.getCenterImage(), 0, 0, imgSize, imgSize, null)
            }
        }
    }

    fun BufferedImage.saveAvatar(file: File) {
        ImageIO.getImageWritersBySuffix("png").next().apply {
            val out = ByteArrayOutputStream()
            output = ImageIO.createImageOutputStream(out)
            defaultWriteParam.apply {
                compressionMode = ImageWriteParam.MODE_EXPLICIT
                compressionQuality = 1f
            }
            write(this@saveAvatar)
            file.mkParent()
            file.writeBytes(out.toByteArray())
        }
    }

    override fun setAvatar(uid: Uid, image: BufferedImage) {
        genAvatar(image, 400).saveAvatar(DataHelper.avatarFile(uid))
        genAvatar(image, 120).saveAvatar(DataHelper.avatarSmallFile(uid))
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