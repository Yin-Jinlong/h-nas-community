package com.yjl.hnas.entity

import com.yjl.hnas.utils.FileUtils
import jakarta.persistence.*
import org.hibernate.annotations.Comment

/**
 * @author YJL
 */
@Table(
    indexes = [
        Index(name = "fid", columnList = "fid"),
        Index(name = "hash", columnList = "hash"),
        Index(name = "type", columnList = "type"),
    ]
)
@Entity
@Comment("文件映射")
data class FileMapping(

    @Id
    @Column(length = VFile.ID_LENGTH)
    @Comment("文件id")
    var fid: VFileId = "",

    @Column(length = VFile.PATH_LENGTH)
    @Comment("文件路径")
    var dataPath: String = "",

    @Column(length = VFile.HASH_LENGTH)
    @Comment("文件hash, base64<<sha256<<data")
    var hash: String = "",

    @Column(length = 32)
    @Comment("类型")
    override var type: String = "",

    @Column(length = 32)
    @Comment("子类型")
    override var subType: String = "",
) : FileWithType {

    companion object {

        val PreviewTypes = listOf(
            "image" to "",
            "video" to "",
        )
    }

    fun toFile() = FileUtils.getData(dataPath)
}
