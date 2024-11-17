package com.yjl.hnas.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment

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
    @Column(length = VFile.HASH_LENGTH)
    @Comment("文件hash, base64<<sha256<<data")
    var hash: String = "",

    @Column(length = VFile.PATH_LENGTH)
    @Comment("文件路径")
    var dataPath: String = "",

    @Column(length = 32)
    @Comment("类型")
    override var type: String = "",

    @Column(length = 32)
    @Comment("子类型")
    override var subType: String = "",

    @Column(columnDefinition = "bigint default(-1)")
    @Comment("文件大小")
    var size: Long = -1
) : FileWithType {

    companion object {

        val PreviewTypes = listOf(
            "image" to "",
            "video" to "",
        )
    }
}
