package io.github.yinjinlong.hnas.config

import com.google.gson.Gson
import io.github.yinjinlong.hnas.fe.AudioFileInfoReader
import io.github.yinjinlong.hnas.fe.FileExtraReader
import io.github.yinjinlong.hnas.fe.FileExtraReaderHelper
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author YJL
 */
@Configuration
class FileExtraConfig {

    @Bean

    fun fileExtraReaderHelper(
        readers: ObjectProvider<FileExtraReader>
    ): FileExtraReaderHelper = FileExtraReaderHelper().apply {
        readers.forEach {
            it.types.forEach { t ->
                registerReader(t, it)
            }
        }
    }

    @Bean
    fun audioExtraReader(
        gson: Gson
    ): FileExtraReader = AudioFileInfoReader(gson)
}
