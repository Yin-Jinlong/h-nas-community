package com.yjl.hnas.data

import com.yjl.hnas.entity.Uid
import java.io.File

/**
 * @author YJL
 */
object DataHelper {
    private var DataRoot = "data"
    private var DataDir = "data/data"
    private var ThumbnailDir = "cache/缩略图"
    private var PreviewDir = "cache/预览图"
    private var AvatarDir = "data/avatar"

    /**
     * 数据目录：data/..
     */
    fun dataSub(path: String) = File(DataRoot, path)

    /**
     * 数据文件：data/data
     */
    fun dataFile(path: String) = File(DataDir, path)

    /**
     * 缩略图：cache/缩略图/...
     */
    fun thumbnailFile(dataPath: String): File = File(ThumbnailDir, "$dataPath.jpg")

    /**
     * 预览图：cache/预览图/...
     */
    fun previewFile(dataPath: String): File = File(PreviewDir, "$dataPath.jpg")

    /**
     * 头像：data/avatar/...
     */
    fun avatarFile(uid: Uid): File = File(AvatarDir, "$uid.jpg")
}
