package io.github.yinjinlong.hnas.config

import io.github.yinjinlong.hnas.tools.CommonTool
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.ChatMemoryRepository
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
import org.springframework.util.ResourceUtils

/**
 * @author YJL
 */
@Configuration
class OllamaConfig {

    @Bean
    fun chatMemoryRepository(): ChatMemoryRepository = InMemoryChatMemoryRepository()

    @Bean
    fun chatMemory(
        repository: ChatMemoryRepository
    ): ChatMemory = MessageWindowChatMemory.builder()
        .chatMemoryRepository(repository)
        .build()

    @Bean
    fun chatClient(
        resourceLoader: ResourceLoader,
        model: OllamaChatModel,
        chatMemory: ChatMemory,
        tools: Array<CommonTool>,
    ): ChatClient = ChatClient.builder(model)
        .defaultAdvisors {
            it.advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
        }
        .defaultSystem(resourceLoader.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "ai-system.md"))
        .defaultTools(*tools)
        .build()

}
