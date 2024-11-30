package com.yjl.hnas.service

import com.yjl.hnas.data.HLSStreamInfo
import com.yjl.hnas.entity.Hash
import com.yjl.hnas.entity.IFileMapping
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

    fun getVideoLiveStream(mapping: IFileMapping, hash: String): List<HLSStreamInfo>
}
