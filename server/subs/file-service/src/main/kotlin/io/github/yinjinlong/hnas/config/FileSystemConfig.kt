package io.github.yinjinlong.hnas.config

import io.github.yinjinlong.hnas.fs.VirtualFileSystemProvider
import io.github.yinjinlong.hnas.service.VirtualFileService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author YJL
 */
@Configuration
class FileSystemConfig {

    @Bean
    fun virtualFileSystemProvider(virtualFileService: VirtualFileService) =
        VirtualFileSystemProvider(virtualFileService)
}
