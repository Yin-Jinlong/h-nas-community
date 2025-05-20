package io.github.yinjinlong.hnas.audio

import io.github.yinjinlong.hnas.data.AudioFileInfo
import io.github.yinjinlong.hnas.data.DataHelper
import io.github.yinjinlong.hnas.entity.Hash
import io.github.yinjinlong.hnas.tika.FileDetector
import io.github.yinjinlong.hnas.utils.MediaSubtypeType
import io.github.yinjinlong.hnas.utils.mkParent
import io.github.yinjinlong.md.sha256
import org.apache.tika.mime.MediaType
import org.jaudiotagger.audio.AudioHeader
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File

/**
 * @author YJL
 */
object AudioInfoHelper {

    /**
     * 保存图片（到文件），如果不存在
     * @param data 图片数据
     * @return hash
     */
    fun saveImage(data: ByteArray): String {
        val hash = Hash(data.sha256).pathSafe
        val coverFile = DataHelper.coverFile(hash)
        if (!coverFile.exists()) {
            coverFile.mkParent()
            coverFile.writeBytes(data)
        }
        return hash
    }

    /**
     * 转换音频信息
     * @param header 音频头
     * @param coverFn 封面文件生成
     */
    fun Tag.toInfo(header: AudioHeader, coverFn: () -> String?) = AudioFileInfo(
        title = getFirst(FieldKey.TITLE),
        subTitle = getFirst(FieldKey.SUBTITLE),
        artists = getFirst(FieldKey.ARTIST),
        cover = coverFn(),
        album = getFirst(FieldKey.ALBUM),
        duration = header.preciseTrackLength.toFloat(),
        year = getFirst(FieldKey.YEAR),
        num = getFirst(FieldKey.TRACK).toIntOrNull(),
        style = getFirst(FieldKey.GENRE),
        bitrate = header.bitRateAsNumber.toInt(),
        comment = getFirst(FieldKey.COMMENT),
        lrc = !getFirst(FieldKey.LYRICS).isNullOrBlank(),
    )

    /**
     * 获取封面数据
     */
    fun getCoverData(file: File): ByteArray? {
        val type = file.inputStream().buffered().use { FileDetector.detect(it, file.name) }
        return when (type.subtype) {
            MediaSubtypeType.AUDIO_MP3 -> Mp3AudioInfoHelper.getCoverData(file)
            MediaSubtypeType.AUDIO_FLAC -> FlacAudioInfoHelper.getCoverData(file)
            else -> null
        }
    }

    /**
     * 获取封面数据，并保存
     * @param file 音频文件，必须存在，否则报错
     */
    fun saveCover(file: File): String? {
        val type = file.inputStream().buffered().use { FileDetector.detect(it, file.name) }
        return when (type.subtype) {
            MediaSubtypeType.AUDIO_MP3 -> Mp3AudioInfoHelper.getCoverData(file)
            MediaSubtypeType.AUDIO_FLAC -> FlacAudioInfoHelper.getCoverData(file)
            else -> null
        }?.let { saveImage(it) }
    }

    /**
     * 获取音频信息
     * @param file 文件
     * @param type 媒体类型
     */
    fun getInfo(file: File, type: MediaType): AudioFileInfo? {
        return when (type.subtype) {
            MediaSubtypeType.AUDIO_MP3 -> Mp3AudioInfoHelper.getInfo(file)
            MediaSubtypeType.AUDIO_FLAC -> FlacAudioInfoHelper.getInfo(file)
            else -> null
        }
    }

    /**
     * 获取音频信息
     * @param file 文件
     * @param type 媒体类型
     */
    fun getLrc(file: File, type: MediaType): String? {
        return when (type.subtype) {
            MediaSubtypeType.AUDIO_MP3 -> Mp3AudioInfoHelper.getLrc(file)
            MediaSubtypeType.AUDIO_FLAC -> FlacAudioInfoHelper.getLrc(file)
            else -> null
        }
    }

}