package io.github.yinjinlong.hnas.config

import io.github.yinjinlong.hnas.tools.NumTool
import io.github.yinjinlong.hnas.tools.TimeTool
import io.github.yinjinlong.hnas.tools.WeatherTool
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.ResourceUtils

/**
 * @author YJL
 */
@Configuration
class OllamaConfig {

    @Bean
    fun chatMemory(): ChatMemory = MessageWindowChatMemory.builder().build()

    @Bean
    fun chatClient(
        context: ApplicationContext,
        model: OllamaChatModel,
        chatMemory: ChatMemory
    ): ChatClient = ChatClient.builder(model)
        .defaultAdvisors {
            it.advisors(MessageChatMemoryAdvisor(chatMemory))
        }
        .defaultSystem(context.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "ai-system.md"))
        .build()

    /**
     * 通用工具
     */
    @Bean
    fun tools(
        numTool: NumTool,
        timeTool: TimeTool,
        weatherTool: WeatherTool
    ): Array<Any> = arrayOf(
        numTool,
        timeTool,
        weatherTool,
    )
}
