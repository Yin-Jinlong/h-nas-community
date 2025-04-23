package io.github.yinjinlong.hnas.audio

import io.github.yinjinlong.hnas.data.DataHelper
import io.github.yinjinlong.hnas.entity.AudioInfo
import io.github.yinjinlong.hnas.entity.FileMapping
import io.github.yinjinlong.hnas.entity.Hash
import io.github.yinjinlong.hnas.tika.FileDetector
import io.github.yinjinlong.hnas.utils.MediaSubtypeType
import io.github.yinjinlong.hnas.utils.mkParent
import io.github.yinjinlong.md.sha256
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
     * @param hash hash
     * @param coverFn 封面文件生成
     */
    fun Tag.toInfo(header: AudioHeader, hash: Hash, coverFn: () -> String?) = AudioInfo(
        hash = hash,
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
        lrc = getFirst(FieldKey.LYRICS)
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
     * @param hash hash
     * @param fm 文件映射
     */
    fun getInfo(hash: Hash, fm: FileMapping): AudioInfo? {
        val file = DataHelper.dataFile(fm.dataPath)
        return when (fm.subType) {
            MediaSubtypeType.AUDIO_MP3 -> Mp3AudioInfoHelper.getInfo(file, hash)
            MediaSubtypeType.AUDIO_FLAC -> FlacAudioInfoHelper.getInfo(file, hash)
            else -> null
        }
    }

}