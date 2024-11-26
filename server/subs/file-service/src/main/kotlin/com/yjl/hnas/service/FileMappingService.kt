package com.yjl.hnas.service

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

    companion object {
        var DataDir = "data"
        var DataDataDir = "data/data"
        var ThumbnailDir = "cache/缩略图"
        var PreviewDir = "cache/预览图"

        fun dataSub(path: String) = File(DataDir, path)
        fun dataFile(path: String) = File(DataDataDir, path)

        fun thumbnailFile(dataPath: String): File = File(ThumbnailDir, "$dataPath.jpg")
        fun previewFile(dataPath: String): File = File(PreviewDir, "$dataPath.jpg")
    }

}
