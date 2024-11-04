package com.yjl.hnas.data

import com.yjl.hnas.entity.VFile

/**
 * 文件信息
 *
 * @property path 文件原始路径
 * @property fileType 文件类型
 * @property type 文件类型
 * @property subType 文件子类型
 * @property preview 预览地址
 * @property createTime 创建时间
 * @property updateTime 更新时间
 * @property size 文件大小
 *
 * @author YJL
 */
data class FileInfo(
    val path: String,
    val fileType: VFile.Type,
    val type: String,
    val subType: String,
    val preview: String?,
    val createTime: Long,
    val updateTime: Long,
    val size: Long
) : Comparable<FileInfo> {
    override fun compareTo(other: FileInfo): Int {
        return path.compareTo(other.path)
    }
}