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
    @Column(length = IVFile.HASH_LENGTH)
    @Comment("文件hash, base64<<sha256<<data")
    override var hash: String = "",

    @Column(length = IVFile.PATH_LENGTH, nullable = false)
    @Comment("文件路径")
    override var dataPath: String = "",

    @Column(length = 32, nullable = false)
    @Comment("类型")
    override var type: String = "",

    @Column(length = 32, nullable = false)
    @Comment("子类型")
    override var subType: String = "",

    @Column(columnDefinition = "bigint default(-1)", nullable = false)
    @Comment("文件大小")
    override var size: Long = -1
) : IFileMapping
