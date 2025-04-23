package io.github.yinjinlong.hnas.option

/**
 * @author YJL
 */
data class PreviewOption(
    var thumbnailSize: Int,
    var previewSize: Int,
    val thumbnailQuality: Float,
    val previewQuality: Float
)
