package io.github.yinjinlong.hnas.controller

import io.github.yinjinlong.hnas.annotation.ShouldLogin
import io.github.yinjinlong.hnas.data.ChatMessageItem
import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.token.Token
import io.github.yinjinlong.hnas.tools.CommonTool
import io.github.yinjinlong.hnas.tools.FileTool
import io.github.yinjinlong.hnas.utils.logger
import io.github.yinjinlong.spring.boot.annotations.SkipHandle
import jakarta.validation.constraints.NotEmpty
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.core.io.ResourceLoader
import org.springframework.util.ResourceUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import reactor.core.publisher.Flux


/**
 * @author YJL
 */
@RestController
@RequestMapping(API.AI)
class ChatController(
    private val resourceLoader: ResourceLoader,
    private val restTemplate: RestTemplate,
    private val model: OllamaChatModel,
    private val chatClient: ChatClient,
    private val chatMemory: ChatMemory,
    private val tools: Array<CommonTool>,
) {

    val logger = ChatController::class.logger()

    private fun chatClientWithCommonToolsBuilder(): ChatClient.Builder = ChatClient.builder(model)
        .defaultAdvisors {
            it.advisors(MessageChatMemoryAdvisor(chatMemory))
        }
        .defaultSystem(resourceLoader.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "ai-system.md"))
        .defaultTools(*tools)

    fun chatId(uid: Uid) = "chat-${uid}"

    @GetMapping("history")
    fun getHistory(
        @ShouldLogin token: Token,
    ): List<ChatMessageItem> {
        logger.info("getHistory ${token.user}")
        return chatMemory.get(chatId(token.user)).map {
            ChatMessageItem(it.messageType.name.lowercase(), it.text)
        }
    }

    @DeleteMapping("history")
    fun clearHistory(
        @ShouldLogin token: Token,
    ) {
        logger.info("clearHistory ${token.user}")
        chatMemory.clear(chatId(token.user))
    }

    @SkipHandle
    @PostMapping("/chat")
    fun chat(
        @ShouldLogin token: Token,
        @RequestBody param: ChatParam,
    ): Flux<String> {
        logger.info("chat ${token.user} ${param.message}")
        return chatClient.let {
            if (param.tool)
                chatClientWithCommonToolsBuilder()
                    .defaultTools(FileTool(restTemplate, token))
                    .build()
            else
                it
        }.prompt()
            .system {
                it.param("tool-status", if (param.tool) "启用" else "禁用")
            }
            .advisors {
                it.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId(token.user))
            }
            .user(param.message)
            .stream()
            .content()
    }

}

data class ChatParam(
    @NotEmpty
    var message: String = "",
    val tool: Boolean = false,
)
