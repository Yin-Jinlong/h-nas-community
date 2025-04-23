package io.github.yinjinlong.hnas.entity

/**
 * @author YJL
 */
interface IAudioInfo : IMediaInfo {

    /**
     * 时长，单位秒
     */
    var duration: Float

    /**
     * 艺术家
     */
    var artists: String?

    /**
     * 专辑
     */
    var album: String?

    /**
     * 年份
     */
    var year: String?

    /**
     * 序号
     */
    var num: Int?

    /**
     * 风格
     */
    var style: String?

    /**
     * 比特率，kbps
     */
    var bitrate: Int

    /**
     * 歌词
     */
    var lrc: String?
}
