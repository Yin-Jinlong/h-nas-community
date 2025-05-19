package io.github.yinjinlong.hnas.entity

import com.google.gson.JsonElement
import java.sql.Timestamp

typealias FileId = Hash

/**
 * @author YJL
 */
interface IVirtualFile {

    var fid: FileId

    var name: String

    var parent: FileId

    var hash: Hash?

    var owner: Uid

    var user: Uid

    var createTime: Timestamp

    var updateTime: Timestamp

    var size: Long

    var extra: JsonElement?

    companion object {
        const val NAME_LENGTH = 128
        const val PATH_LENGTH = 1024
        const val ID_LENGTH = 16
        const val HASH_LENGTH = 32
        const val TYPE_LENGTH = 32
        const val SUB_TYPE_LENGTH = 96
    }

    enum class Type {
        /**
         * 目录
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
