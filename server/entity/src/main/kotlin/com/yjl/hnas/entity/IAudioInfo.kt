package com.yjl.hnas.entity

/**
 * @author YJL
 */
interface IAudioInfo : IMediaInfo {

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
    var year: Short?

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
    var bitRate: Int

}
