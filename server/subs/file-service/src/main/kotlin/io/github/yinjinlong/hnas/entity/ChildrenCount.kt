package io.github.yinjinlong.hnas.entity

import io.github.yinjinlong.hnas.converter.HashConverter
import io.github.yinjinlong.hnas.usertype.HashUserType
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.Type

/**
 * @author YJL
 */
@Entity
@Table
@Comment("子文件数量")
class ChildrenCount(

    @Id
    @Type(value = HashUserType::class)
    @Convert(converter = HashConverter::class)
    @Column(columnDefinition = "binary(${IVirtualFile.ID_LENGTH})")
    @Comment("文件id")
    override var fid: FileId = Hash(),

    @Column(nullable = false)
    @Comment("子文件数量，当前目录")
    override var subCount: Int = 0,

    @Column(nullable = false)
    @Comment("子文件数量，递归所有")
    override var subsCount: Int = 0
) : IChildrenCount
