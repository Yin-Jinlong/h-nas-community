package com.yjl.hnas.entity

/**
 * @author YJL
 */
interface IChildrenCount {

    var fid: FileId

    /**
     * 子文件数量。仅当前
     */
    var subCount: Int

    /**
     * 子文件数量，递归。包括所有
     */
    var subsCount: Int

}
