package io.github.yinjinlong.hnas.entity

import com.google.gson.JsonElement
import io.github.yinjinlong.hnas.annotation.FulltextIndex
import io.github.yinjinlong.hnas.converter.HashConverter
import io.github.yinjinlong.hnas.usertype.HashUserType
import io.github.yinjinlong.hnas.usertype.JsonUserType
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
        Index(name = "hash", columnList = "hash"),
        Index(name = "owner", columnList = "owner"),
        Index(name = "user", columnList = "user"),
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
    @Column(columnDefinition = "binary(${IVirtualFile.ID_LENGTH}) default (UUID_TO_BIN(UUID(),true))")
    @Comment("UUID")
    override var fid: FileId = Hash(),

    @FulltextIndex
    @Column(length = IVirtualFile.NAME_LENGTH, nullable = false)
    @Comment("文件名")
    override var name: String = "",

    @Type(value = HashUserType::class)
    @Convert(converter = HashConverter::class)
    @Column(columnDefinition = "binary(${IVirtualFile.ID_LENGTH})", nullable = false)
    @Comment("所在目录")
    override var parent: FileId = Hash(IVirtualFile.ID_LENGTH),

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

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    @Comment("文件创建时间")
    override var createTime: Timestamp = Timestamp(0),

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp on update current_timestamp")
    @Comment("文件修改时间")
    override var updateTime: Timestamp = Timestamp(0),

    @Column(nullable = false)
    @Comment("文件/目录大小")
    override var size: Long = 0,

    @Type(value = JsonUserType::class)
    @Column(columnDefinition = "json")
    @Comment("文件附加信息")
    override var extra: JsonElement? = null,
) : IVirtualFile
