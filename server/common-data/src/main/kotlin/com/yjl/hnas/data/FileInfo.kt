package com.yjl.hnas.data

import com.yjl.hnas.entity.IVFile

/**
 * 文件信息
 *
 * @property name 文件名
 * @property dir 文件路径
 * @property fileType 文件类型
 * @property type 文件类型
 * @property subType 文件子类型
 * @property preview 预览
 * @property createTime 创建时间
 * @property updateTime 更新时间
 * @property size 文件大小
 *
 * @author YJL
 */
data class FileInfo(
    val name: String,
    val dir: String,
    val fileType: IVFile.Type,
    val type: String,
    val subType: String,
    val preview: String?,
    val createTime: Long,
    val updateTime: Long,
    val size: Long
) : Comparable<FileInfo> {
    override fun compareTo(other: FileInfo): Int {
        val tr = fileType.compareTo(other.fileType)
        return if (tr != 0) tr else name.compareTo(other.name)
    }
}