package com.yjl.hnas.service

import com.yjl.hnas.data.ChapterInfo
import com.yjl.hnas.data.HLSStreamInfo
import com.yjl.hnas.entity.Hash
import com.yjl.hnas.entity.IFileMapping
import com.yjl.hnas.fs.VirtualPath
import java.io.File

/**
 * @author YJL
 */
interface FileMappingService {

    /**
     * 获取映射
     */
    fun getMapping(hash: Hash): IFileMapping?

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
    fun getVideoLiveStream(path: VirtualPath): HLSStreamInfo?
}
