package com.yjl.hnas.data

/**
 * 文件信息
 *
 * @property preview 预览
 * @property type 文件类型
 * @property subType 子类型
 * @author YJL
 */
data class FileExtraInfo(
    val preview: String? = null,
    val type: String,
    val subType: String
)
