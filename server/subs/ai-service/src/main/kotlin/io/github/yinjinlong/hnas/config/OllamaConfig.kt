package io.github.yinjinlong.hnas.config

import org.springframework.ai.autoconfigure.ollama.OllamaConnectionDetails
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.ollama.api.OllamaApi
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
import org.springframework.util.ResourceUtils
import org.springframework.web.client.RestClient
import org.springframework.web.reactive.function.client.WebClient

/**
 * @author YJL
 */
@Configuration
class OllamaConfig {

    @Bean
    fun ollamaApi(
        connectionDetails: OllamaConnectionDetails,
        restClientBuilderProvider: ObjectProvider<RestClient.Builder>,
        webClientBuilderProvider: ObjectProvider<WebClient.Builder>
    ): OllamaApi {
        return OllamaApi.builder()
            .baseUrl(connectionDetails.baseUrl)
            .restClientBuilder(restClientBuilderProvider.getIfAvailable { RestClient.builder() })
            .webClientBuilder(webClientBuilderProvider.getIfAvailable { WebClient.builder() })
            .build()
    }

    @Bean
    fun chatMemory(): ChatMemory = MessageWindowChatMemory.builder().build()

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
