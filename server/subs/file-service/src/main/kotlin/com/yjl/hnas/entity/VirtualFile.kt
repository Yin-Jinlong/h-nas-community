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
        Index(name = "parent", columnList = "parent"),
        Index(name = "name", columnList = "name"),
        Index(name = "hash", columnList = "hash"),
        Index(name = "owner", columnList = "owner"),
    ]
)
data class VirtualFile(

    /**
     * 文件id
     *
     * - 公开文件 `"" path`
     * - 私有文件 `uid path`
     */
    @Id
    @Column(length = IVirtualFile.ID_LENGTH)
    @Comment("文件id, base64<<sha256<<(access,full_path)")
    override var fid: VFileId = "",

    @Column(length = IVirtualFile.NAME_LENGTH, nullable = false)
    @Comment("文件名")
    override var name: String = "",

    @Column(length = IVirtualFile.ID_LENGTH, nullable = false)
    @Comment("所在目录")
    override var parent: VFileId = "",

    @Column(length = IVirtualFile.HASH_LENGTH)
    @Comment("文件hash")
    override var hash: String? = null,

    @Column(nullable = false)
    @Comment("文件拥有者")
    override var owner: Uid = 0,

    @Column(nullable = false)
    @Comment("文件创建时间")
    override var createTime: Timestamp = Timestamp(0),

    @Column(nullable = false)
    @Comment("文件修改时间")
    override var updateTime: Timestamp = Timestamp(0),

    @Column(nullable = false)
    @Comment("文件/目录大小")
    override var size: Long = 0
) : IVirtualFile
