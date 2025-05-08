package io.github.yinjinlong.hnas.config

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.InMemoryChatMemory
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
    fun chatMemory(): ChatMemory = InMemoryChatMemory()

    @Bean
    fun chatClient(
        resourceLoader: ResourceLoader,
        model: OllamaChatModel,
        chatMemory: ChatMemory
    ): ChatClient = ChatClient.builder(model)
        .defaultAdvisors {
            it.advisors(MessageChatMemoryAdvisor(chatMemory))
        }
        .defaultSystem(resourceLoader.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "ai-system.md"))
        .build()

}
