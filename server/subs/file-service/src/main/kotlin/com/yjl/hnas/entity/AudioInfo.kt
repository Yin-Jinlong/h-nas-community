package com.yjl.hnas.entity

import com.yjl.hnas.converter.HashConverter
import com.yjl.hnas.usertype.HashUserType
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.Type

/**
 * @author YJL
 */
@Table(
    indexes = [
        Index(name = "title", columnList = "title"),
        Index(name = "album", columnList = "album"),
        Index(name = "year", columnList = "year"),
    ]
)
@Entity
@Comment("音乐信息")
class AudioInfo(

    @Id
    @Type(value = HashUserType::class)
    @Convert(converter = HashConverter::class)
    @Column(columnDefinition = "binary(${IVirtualFile.HASH_LENGTH})")
    @Comment("文件id")
    override var hash: Hash = Hash(),

    @Column(length = IMediaInfo.ITEM_LENGTH)
    @Comment("标题")
    override var title: String? = null,

    @Column(length = IMediaInfo.ITEM_LENGTH)
    @Comment("副标题")
    override var subTitle: String? = null,

    @Column(length = IMediaInfo.ITEM_LENGTH)
    @Comment("艺术家")
    override var artists: String? = null,

    @Column(length = IMediaInfo.ITEM_LENGTH)
    @Comment("封面")
    override var cover: String? = null,

    @Column(length = IMediaInfo.ITEM_LENGTH)
    @Comment("专辑")
    override var album: String? = null,

    @Column(nullable = false)
    @Comment("时长,秒")
    override var duration: Float = 0f,

    @Column(length = 12)
    @Comment("年份")
    override var year: String? = null,

    @Column
    @Comment("序号")
    override var num: Int? = null,

    @Column(length = IMediaInfo.ITEM_LENGTH)
    @Comment("风格")
    override var style: String? = null,

    @Column(nullable = false)
    @Comment("比特率,kbps")
    override var bitrate: Int = -1,

    @Column(length = IMediaInfo.ITEM_LENGTH)
    @Comment("备注")
    override var comment: String? = null,

    @Column(length = IMediaInfo.LRC_LENGTH)
    @Comment("歌词")
    override var lrc: String? = null
) : IAudioInfo
