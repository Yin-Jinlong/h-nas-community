package com.yjl.hnas.entity

import com.yjl.hnas.converter.HashConverter
import com.yjl.hnas.usertype.HashUserType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import org.hibernate.annotations.Type

/**
 * @author YJL
 */
@Table
@Entity
@Comment("音乐信息")
class AudioInfo(

    @Id
    @Type(value = HashUserType::class)
    @Convert(converter = HashConverter::class)
    @Column(columnDefinition = "binary(${IVirtualFile.ID_LENGTH})")
    @Comment("文件id")
    override var fid: Hash = Hash(),

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

    @Column
    @Comment("年份")
    override var year: Short? = null,

    @Column
    @Comment("序号")
    override var num: Int? = null,

    @Column(length = IMediaInfo.ITEM_LENGTH)
    @Comment("风格")
    override var style: String? = null,

    @Column
    @Comment("比特率,kbps")
    override var bitRate: Int = -1,

    @Column(length = IMediaInfo.ITEM_LENGTH)
    @Comment("备注")
    override var comment: String? = null
) : IAudioInfo
