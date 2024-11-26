package com.yjl.hnas.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author YJL
 */
@ConfigurationProperties(prefix = "app.preview")
data class PreviewProperties(
    /**
     * 缩略图大小
     */
    var thumbnailSize: Int = 400,
    /**
     * 预览图大小
     */
    var previewSize: Int = 1200,
    /**
     * 缩略图质量
     */
    var thumbnailQuality: Float = 0.3f,
    /**
     * 预览图质量
     */
    var previewQuality: Float = 0.9f,
)
