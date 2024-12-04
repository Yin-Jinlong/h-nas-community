package com.yjl.hnas.data

import com.yjl.hnas.entity.IAudioInfo

/**
 * @author YJL
 */
data class AudioFileInfo(
    /**
     * 音频文件路径
     */
    val path: String,
    /**
     * 标题
     */
    val title: String?,
    /**
     * 副标题
     */
    val subTitle: String?,
    /**
     * 艺术家
     */
    val artists: String?,
    /**
     * 封面
     */
    val cover: String?,
    /**
     * 时长
     */
    val duration: Float,
    /**
     * 专辑
     */
    val album: String?,
    /**
     * 年份
     */
    val year: Short?,
    /**
     * 序号
     */
    val num: Int?,
    /**
     * 风格
     */
    val style: String?,
    /**
     * 比特率,kbps
     */
    val bitrate: Int,
    /**
     * 备注
     */
    val comment: String?,
) {
    companion object {
        fun of(path: String, info: IAudioInfo): AudioFileInfo {
            return AudioFileInfo(
                path,
                info.title,
                info.subTitle,
                info.artists,
                path,
                info.duration,
                info.album,
                info.year,
                info.num,
                info.style,
                info.bitrate,
                info.comment
            )
        }
    }
}