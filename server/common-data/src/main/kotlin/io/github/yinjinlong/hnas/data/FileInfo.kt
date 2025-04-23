package io.github.yinjinlong.hnas.data

import io.github.yinjinlong.hnas.entity.IVirtualFile

/**
 * 文件信息
 *
 * @property name 文件名
 * @property dir 文件路径
 * @property fileType 文件类型
 * @property mediaType 文件媒体类型
 * @property createTime 创建时间
 * @property updateTime 更新时间
 * @property size 文件大小
 *
 * @author YJL
 */
data class FileInfo(
    val name: String,
    val dir: String,
    val fileType: IVirtualFile.Type,
    val mediaType: String?,
    val createTime: Long,
    val updateTime: Long,
    val size: Long
) : Comparable<FileInfo> {
    override fun compareTo(other: FileInfo): Int {
        val tr = fileType.compareTo(other.fileType)
        return if (tr != 0) tr else name.compareTo(other.name)
    }
}