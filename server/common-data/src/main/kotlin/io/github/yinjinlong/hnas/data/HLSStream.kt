package io.github.yinjinlong.hnas.data

/**
 * @author YJL
 */
data class HLSStream(
    val width: Int,
    val height: Int,
    val bitrate: Int,
    /**
     * 进度,[0-1000]
     * null 表示未生成
     */
    val progress: Int?,
)
