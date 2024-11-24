package com.yjl.hnas.service

import com.yjl.hnas.entity.IFileMapping
import java.io.File

/**
 * @author YJL
 */
interface FileMappingService {

    fun getMapping(hash: String): IFileMapping?

    fun deleteMapping(hash: String)

    fun getSize(hash: String): Long

    fun getPreviewFile(mapping: IFileMapping): File?

    fun getPreview(mapping: IFileMapping): String?

    companion object {
        var DataDir = "data"
        var DataDataDir = "data/data"
        var PreviewDir = "cache/缩略图"

        fun dataFile(path: String) = File(DataDir, path)
        fun dataDataFile(path: String) = File(DataDataDir, path)

        fun previewFile(dataPath: String): File = File(PreviewDir, "$dataPath.jpg")
    }

}
