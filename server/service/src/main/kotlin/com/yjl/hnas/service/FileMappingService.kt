package com.yjl.hnas.service

import com.yjl.hnas.entity.IFileMapping
import com.yjl.hnas.fs.PubPath
import java.io.File

/**
 * @author YJL
 */
interface FileMappingService {

    fun addMapping(path: PubPath, size: Long, hash: String)

    fun getMapping(hash: String): IFileMapping?

    fun deleteMapping(hash: String)

    fun getSize(hash: String): Long

    fun getPreviewFile(mapping: IFileMapping): File?

    fun getPreview(mapping: IFileMapping): String?

    companion object {
        var DataDir = "data"
        var PreviewDir = "cache/缩略图"

        fun dataFile(path: String) = File(DataDir, path)

        fun previewFile(path: String) = File(PreviewDir, path)
    }

}
