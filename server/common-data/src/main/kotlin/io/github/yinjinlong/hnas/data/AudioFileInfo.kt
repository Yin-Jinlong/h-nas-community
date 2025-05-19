package io.github.yinjinlong.hnas.data

/**
 * @author YJL
 */
data class AudioFileInfo(
    /**
     * 音频文件路径
     */
    @Transient
    val path: String = "",
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
    val year: String?,
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
    /**
     * 歌词
     */
    val lrc: Boolean?
)
