package com.yjl.hnas.config

import com.yjl.hnas.option.PreviewOption
import com.yjl.hnas.preview.FilePreviewGenerator
import com.yjl.hnas.preview.ImagePreviewGenerator
import com.yjl.hnas.preview.PreviewGeneratorFactory
import com.yjl.hnas.preview.XMindPreviewGenerator
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author YJL
 */
@Configuration
@EnableConfigurationProperties(PreviewProperties::class)
class PreviewConfig {

    @Bean
    fun previewOption(previewProperties: PreviewProperties) = PreviewOption(
        previewSize = previewProperties.previewSize,
        thumbnailSize = previewProperties.thumbnailSize,
        thumbnailQuality = previewProperties.thumbnailQuality,
        previewQuality = previewProperties.previewQuality
    )

    @Bean
    fun previewGeneratorFactory(
        generators: ObjectProvider<FilePreviewGenerator>
    ) = PreviewGeneratorFactory().apply {
        generators.forEach {
            it.types.forEach { t ->
                registerGenerator(t, it)
            }
        }
    }

    @Bean
    fun imagePreviewGenerator(): FilePreviewGenerator = ImagePreviewGenerator()

    @Bean
    fun xMindPreviewGenerator(): FilePreviewGenerator = XMindPreviewGenerator()

}