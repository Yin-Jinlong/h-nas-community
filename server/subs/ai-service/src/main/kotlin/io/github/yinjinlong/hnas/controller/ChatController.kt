package io.github.yinjinlong.hnas.controller

import io.github.yinjinlong.hnas.annotation.ShouldLogin
import io.github.yinjinlong.hnas.data.ChatMessageItem
import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.token.Token
import io.github.yinjinlong.hnas.tools.FileTool
import io.github.yinjinlong.hnas.utils.logger
import io.github.yinjinlong.spring.boot.annotations.SkipHandle
import jakarta.validation.constraints.NotEmpty
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import reactor.core.publisher.Flux


/**
 * @author YJL
 */
@RestController
@RequestMapping(API.AI)
class ChatController(
    private val restTemplate: RestTemplate,
    private val chatClient: ChatClient,
    private val chatMemory: ChatMemory,
) {

    val logger = ChatController::class.logger()

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
        return chatClient.prompt()
            .tools(FileTool(restTemplate, token))
            .system {

            }
            .advisors {
                it.param(ChatMemory.CONVERSATION_ID, chatId(token.user))
            }
            .user(param.message)
            .stream()
            .content()
    }

}

data class ChatParam(
    @NotEmpty
    var message: String = "",
)
