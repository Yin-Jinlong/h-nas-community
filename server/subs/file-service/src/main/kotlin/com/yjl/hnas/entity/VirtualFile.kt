package com.yjl.hnas.entity

import com.yjl.hnas.converter.HashConverter
import com.yjl.hnas.usertype.HashUserType
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.Type
import java.sql.Timestamp

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
        Index(name = "user", columnList = "user"),
        Index(name = "media_type", columnList = "mediaType"),
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
    @Type(value = HashUserType::class)
    @Convert(converter = HashConverter::class)
    @Column(columnDefinition = "binary(${IVirtualFile.ID_LENGTH})")
    @Comment("文件id, base64<<sha256<<(access,full_path)")
    override var fid: FileId = Hash(),

    @Column(length = IVirtualFile.NAME_LENGTH, nullable = false)
    @Comment("文件名")
    override var name: String = "",

    @Type(value = HashUserType::class)
    @Convert(converter = HashConverter::class)
    @Column(columnDefinition = "binary(${IVirtualFile.ID_LENGTH})", nullable = false)
    @Comment("所在目录")
    override var parent: FileId = Hash(),

    @Convert(converter = HashConverter::class)
    @Column(columnDefinition = "binary(${IVirtualFile.HASH_LENGTH})")
    @Comment("文件hash")
    override var hash: Hash? = null,

    @Column(nullable = false)
    @Comment("文件拥有者")
    override var owner: Uid = 0,

    @Column(nullable = false)
    @Comment("文件所在用户")
    override var user: Uid = 0,

    @Column(length = IVirtualFile.TYPE_LENGTH + IVirtualFile.SUB_TYPE_LENGTH, nullable = false)
    @Comment("文件类型")
    override var mediaType: String = "",

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
