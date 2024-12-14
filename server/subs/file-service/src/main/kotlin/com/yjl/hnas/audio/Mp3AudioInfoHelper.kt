package com.yjl.hnas.audio

import com.yjl.hnas.audio.AudioInfoHelper.saveImage
import com.yjl.hnas.audio.AudioInfoHelper.toInfo
import com.yjl.hnas.entity.AudioInfo
import com.yjl.hnas.entity.Hash
import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.audio.mp3.MP3FileReader
import org.jaudiotagger.tag.id3.AbstractID3v2Frame
import org.jaudiotagger.tag.id3.AbstractID3v2Tag
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC
import java.io.File

/**
 * @author YJL
 */
object Mp3AudioInfoHelper {

    /**
     * 获取图片帧
     */
    private fun AbstractID3v2Tag.getCoverFrame(): FrameBodyAPIC? {
        val frame = getFrame("APIC") as? AbstractID3v2Frame? ?: return null
        return (frame.body as FrameBodyAPIC)
    }

    /**
     * 获取封面图片
     */
    private fun getMp3Cover(tag: AbstractID3v2Tag): String? {
        val frame = tag.getCoverFrame() ?: return null
        val data = frame.imageData
        return saveImage(data)
    }

    /**
     * 获取封面图片数据
     */
    fun getCoverData(file: File): ByteArray? {
        val af = MP3FileReader().read(file) as MP3File
        if (!af.hasID3v2Tag())
            return null
        val tag = af.iD3v2Tag!!
        val frame = tag.getCoverFrame()
        return frame?.imageData
    }

    /**
     * 获取音频信息
     */
    fun getInfo(file: File, hash: Hash): AudioInfo? {
        val af = MP3FileReader().read(file) as MP3File
        if (!af.hasID3v2Tag())
            return null
        val tag = af.iD3v2Tag!!
        return tag.toInfo(af.audioHeader, hash) {
            getMp3Cover(tag)
        }
    }

}