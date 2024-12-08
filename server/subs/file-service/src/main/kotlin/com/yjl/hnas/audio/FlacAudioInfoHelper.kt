package com.yjl.hnas.audio

import com.yjl.hnas.audio.AudioInfoHelper.saveImage
import com.yjl.hnas.audio.AudioInfoHelper.toInfo
import com.yjl.hnas.entity.AudioInfo
import com.yjl.hnas.entity.Hash
import org.jaudiotagger.audio.flac.FlacFileReader
import org.jaudiotagger.tag.flac.FlacTag
import java.io.File

/**
 * @author YJL
 */
object FlacAudioInfoHelper {

    private fun getFlacCover(tag: FlacTag): String? {
        val image = tag.images.firstOrNull() ?: return null
        return saveImage(image.imageData)
    }

    fun getCoverData(file: File): ByteArray? {
        val af = FlacFileReader().read(file)
        val tag = af.tag as FlacTag
        return tag.images.firstOrNull()?.imageData
    }

    fun getInfo(file: File, hash: Hash): AudioInfo {
        val af = FlacFileReader().read(file)
        val tag = af.tag as FlacTag
        return tag.toInfo(af.audioHeader, hash) {
            getFlacCover(tag)
        }
    }

}