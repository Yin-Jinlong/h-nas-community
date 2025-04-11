package com.yjl.hnas.entity

/**
 * @author YJL
 */
interface IMediaInfo {

    /**
     * 文件id
     */
    var fid: Hash

    /**
     * 标题
     */
    var title: String?

    /**
     * 副标题
     */
    var subTitle: String?

    /**
     * 封面
     */
    var cover: String?

    /**
     * 备注
     */
    var comment: String?

    companion object {
        const val ITEM_LENGTH = 256
        const val LRC_LENGTH = 4 * 1024
    }
}