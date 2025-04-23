package io.github.yinjinlong.hnas.audio

import io.github.yinjinlong.hnas.audio.AudioInfoHelper.saveImage
import io.github.yinjinlong.hnas.audio.AudioInfoHelper.toInfo
import io.github.yinjinlong.hnas.entity.AudioInfo
import io.github.yinjinlong.hnas.entity.Hash
import org.jaudiotagger.audio.flac.FlacFileReader
import org.jaudiotagger.tag.flac.FlacTag
import java.io.File

/**
 * @author YJL
 */
object FlacAudioInfoHelper {

    /**
     * 获取封面，图片列表第一个
     * @param tag FlacTag
     */
    private fun getFlacCover(tag: FlacTag): String? {
        val image = tag.images.firstOrNull() ?: return null
        return saveImage(image.imageData)
    }

    /**
     * 获取封面数据
     * @param file 文件
     */
    fun getCoverData(file: File): ByteArray? {
        val af = FlacFileReader().read(file)
        val tag = af.tag as FlacTag
        return tag.images.firstOrNull()?.imageData
    }

    /**
     * 获取音频信息
     * @param file 文件
     * @param hash 文件hash
     */
    fun getInfo(file: File, hash: Hash): AudioInfo {
        val af = FlacFileReader().read(file)
        val tag = af.tag as FlacTag
        return tag.toInfo(af.audioHeader, hash) {
            getFlacCover(tag)
        }
    }

}