package com.yjl.hnas.config

import com.yjl.hnas.preview.FilePreviewGenerator
import com.yjl.hnas.preview.ImagePreviewGenerator
import com.yjl.hnas.preview.PreviewGeneratorFactory
import com.yjl.hnas.preview.XMindPreviewGenerator
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author YJL
 */
@Configuration
class PreviewConfig {

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