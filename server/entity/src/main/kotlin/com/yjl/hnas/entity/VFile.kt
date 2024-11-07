package com.yjl.hnas.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.sql.Timestamp

typealias VFileId = String

/**
 * @author YJL
 */
@Entity
@Table(
    indexes = [
        Index(name = "fid", columnList = "name"),
        Index(name = "fid", columnList = "parent"),
        Index(name = "owner", columnList = "owner"),
        Index(name = "type", columnList = "type"),
    ]
)
data class VFile(

    /**
     * 文件id
     *
     * - 公开文件 `"" path`
     * - 私有文件 `uid path`
     */
    @Id
    @Column(length = ID_LENGTH)
    @Comment("文件id, base64<<sha256<<(access,full_path)")
    var fid: VFileId = "",

    @Column(length = NAME_LENGTH, nullable = false)
    @Comment("文件名")
    var name: String = "",

    @Column(length = ID_LENGTH, nullable = true)
    @Comment("所在目录")
    var parent: VFileId? = null,

    @Column(nullable = false)
    @Comment("文件拥有者")
    var owner: Uid = 0,

    @Column(nullable = false)
    @Comment("文件创建时间")
    var createTime: Timestamp = Timestamp(0),

    @Column(nullable = false)
    @Comment("文件修改时间")
    var updateTime: Timestamp = Timestamp(0),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("文件类型")
    var type: Type = Type.FILE,
) {
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

    fun isFile() = type == Type.FILE

    fun isFolder() = type == Type.FOLDER
}
