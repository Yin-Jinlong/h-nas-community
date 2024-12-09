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

    fun getMapping(hash: Hash): IFileMapping?

    fun deleteMapping(hash: Hash)

    fun getSize(hash: Hash): Long

    fun getPreviewFile(mapping: IFileMapping): File?

    fun getThumbnail(mapping: IFileMapping): String?

    fun getPreview(mapping: IFileMapping): String?

    fun getVideoChapters(path: VirtualPath): List<ChapterInfo>

    fun getVideoLiveStream(path: VirtualPath): HLSStreamInfo?
}
