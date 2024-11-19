package com.yjl.hnas.entity

import java.sql.Timestamp

typealias VFileId = String

/**
 * @author YJL
 */
interface IVFile {

    /**
     * 文件id
     *
     * - 公开文件 `"" path`
     * - 私有文件 `uid path`
     */
    var fid: VFileId

    var name: String

    var parent: VFileId?

    var hash: String?

    var owner: Uid

    var createTime: Timestamp

    var updateTime: Timestamp

    var size: Long

    companion object {
        const val NAME_LENGTH = 128
        const val PATH_LENGTH = 1024
        const val ID_LENGTH = 45
        const val HASH_LENGTH = 45
    }

    enum class Type {
        /**
         * 文件夹
         */
        FOLDER,

        /**
         * 文件
         */
        FILE
    }

    val type: Type
        get() = if (hash == null) Type.FOLDER else Type.FILE

    fun isFile() = hash != null

    fun isFolder() = hash == null
}
