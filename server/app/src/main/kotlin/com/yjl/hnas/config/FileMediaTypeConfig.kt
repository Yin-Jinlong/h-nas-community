package com.yjl.hnas.config

import com.yjl.hnas.utils.mimeType
import io.github.yinjinlong.spring.boot.messageconverter.FileMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import java.io.File

/**
 * @author YJL
 */
@Configuration
class FileMediaTypeConfig {

    @Bean
    fun fileMediaType() = object : FileMessageConverter.FileMediaTypeGetter {
        override fun getMediaType(file: File): MediaType {
            return MediaType.parseMediaType(file.mimeType)
        }
    }

}