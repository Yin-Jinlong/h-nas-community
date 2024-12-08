package com.yjl.hnas.audio

import com.yjl.hnas.data.DataHelper
import com.yjl.hnas.entity.AudioInfo
import com.yjl.hnas.entity.FileMapping
import com.yjl.hnas.entity.Hash
import com.yjl.hnas.tika.FileDetector
import com.yjl.hnas.utils.MediaSubtypeType
import com.yjl.hnas.utils.mkParent
import io.github.yinjinlong.md.sha256
import org.jaudiotagger.audio.AudioHeader
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File

/**
 * @author YJL
 */
object AudioInfoHelper {

    fun saveImage(data: ByteArray): String {
        val hash = Hash(data.sha256).pathSafe
        val coverFile = DataHelper.coverFile(hash)
        if (!coverFile.exists()) {
            coverFile.mkParent()
            coverFile.writeBytes(data)
        }
        return hash
    }

    fun Tag.toInfo(header: AudioHeader, hash: Hash, coverFn: () -> String?) = AudioInfo(
        fid = hash,
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
     */
    fun saveCover(file: File): String? {
        val type = file.inputStream().buffered().use { FileDetector.detect(it, file.name) }
        return when (type.subtype) {
            MediaSubtypeType.AUDIO_MP3 -> Mp3AudioInfoHelper.getCoverData(file)
            MediaSubtypeType.AUDIO_FLAC -> FlacAudioInfoHelper.getCoverData(file)
            else -> null
        }?.let { saveImage(it) }
    }

    fun getInfo(hash: Hash, fm: FileMapping): AudioInfo? {
        val file = DataHelper.dataFile(fm.dataPath)
        return when (fm.subType) {
            MediaSubtypeType.AUDIO_MP3 -> Mp3AudioInfoHelper.getInfo(file, hash)
            MediaSubtypeType.AUDIO_FLAC -> FlacAudioInfoHelper.getInfo(file, hash)
            else -> null
        }
    }

}