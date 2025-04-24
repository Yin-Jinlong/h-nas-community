package io.github.yinjinlong.hnas.service

import io.github.yinjinlong.hnas.data.ChapterInfo
import io.github.yinjinlong.hnas.data.HLSStreamInfo
import io.github.yinjinlong.hnas.data.HLSStreamList
import io.github.yinjinlong.hnas.entity.Hash
import io.github.yinjinlong.hnas.entity.IFileMapping
import io.github.yinjinlong.hnas.fs.VirtualPath
import java.io.File

/**
 * @author YJL
 */
interface FileMappingService {

    /**
     * 获取映射
     */
    fun getMapping(hash: Hash): IFileMapping?

    fun getMediaType(hash: Hash): String

    /**
     * 删除映射
     */
    fun deleteMapping(hash: Hash)

    /**
     * 获取文件大小
     */
    fun getSize(hash: Hash): Long

    /**
     * 获取预览文件
     */
    fun getPreviewFile(mapping: IFileMapping): File?

    /**
     * 获取缩略图
     */
    fun getThumbnail(mapping: IFileMapping): String?

    /**
     * 获取预览文件路径，给前端的
     */
    fun getPreview(mapping: IFileMapping): String?

    /**
     * 获取视频章节
     */
    fun getVideoChapters(path: VirtualPath): List<ChapterInfo>

    /**
     * 获取视频流信息
     */
    fun getVideoLiveStreams(path: VirtualPath): List<HLSStreamList>

    /**
     * 获取视频流信息
     */
    fun getVideoLiveStreamInfo(path: VirtualPath, codec: String, bitrate: Int): HLSStreamInfo

    /**
     * 获取视频流信息
     */
    fun getVideoLiveStreamM3u8(path: VirtualPath, codec: String, bitrate: Int,private: Boolean): StringBuilder

    /**
     * 获取视频流信息
     */
    fun getVideoLiveStreamFile(path: VirtualPath, codec: String, bitrate: Int,index: String): File
}
