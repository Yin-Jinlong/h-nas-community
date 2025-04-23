package io.github.yinjinlong.hnas.entity

import io.github.yinjinlong.hnas.converter.HashConverter
import io.github.yinjinlong.hnas.usertype.HashUserType
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.Type

/**
 * @author YJL
 */
@Table(
    indexes = [
        Index(name = "type", columnList = "type"),
    ]
)
@Entity
@Comment("文件映射")
data class FileMapping(

    @Id
    @Type(value = HashUserType::class)
    @Convert(converter = HashConverter::class)
    @Column(columnDefinition = "binary(${IVirtualFile.HASH_LENGTH})")
    @Comment("文件hash, sha256<<data")
    override var hash: Hash = Hash(),

    @Column(length = IVirtualFile.PATH_LENGTH, nullable = false)
    @Comment("文件路径")
    override var dataPath: String = "",

    @Column(length = IVirtualFile.TYPE_LENGTH, nullable = false)
    @Comment("类型")
    override var type: String = "",

    @Column(length = IVirtualFile.SUB_TYPE_LENGTH, nullable = false)
    @Comment("子类型")
    override var subType: String = "",

    @Column(nullable = false)
    @Comment("有效预览")
    override var preview: Boolean = true,

    @Column(columnDefinition = "bigint default(-1)", nullable = false)
    @Comment("文件大小")
    override var size: Long = -1
) : IFileMapping
