package com.yjl.hnas.config

import com.yjl.hnas.fs.PubFileSystemProvider
import com.yjl.hnas.fs.UserFileSystemProvider
import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.service.PubFileService
import com.yjl.hnas.service.UserFileService
import com.yjl.hnas.service.VirtualFileService
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

    @Bean
    fun userFileSystemProvider(userFileService: UserFileService) =
        UserFileSystemProvider(userFileService)

    @Bean
    fun pubFileSystemProvider(pubFileService: PubFileService) =
        PubFileSystemProvider(pubFileService)
}