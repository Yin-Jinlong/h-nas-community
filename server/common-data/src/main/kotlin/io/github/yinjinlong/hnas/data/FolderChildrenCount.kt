package io.github.yinjinlong.hnas.data

/**
 * @author YJL
 */
data class FolderChildrenCount(
    /**
     * 目录路径
     */
    val dir: String,
    /**
     * 子文件数量
     */
    val subCount: Int,
    /**
     * 子文件数量，递归，所有
     */
    val subsCount: Int
)
