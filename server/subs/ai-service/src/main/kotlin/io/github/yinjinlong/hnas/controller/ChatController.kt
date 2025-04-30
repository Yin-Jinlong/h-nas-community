package io.github.yinjinlong.hnas.controller

import io.github.yinjinlong.hnas.annotation.ShouldLogin
import io.github.yinjinlong.hnas.data.ChatMessageItem
import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.token.Token
import io.github.yinjinlong.spring.boot.annotations.SkipHandle
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotEmpty
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

/**
 * @author YJL
 */
@RestController
@RequestMapping(API.AI)
class ChatController(
    private val chatClient: ChatClient,
    private val chatMemory: ChatMemory,
    @Qualifier("tools")
    private val tools: Array<Any>,
) {

    data class ChatParam(
        @NotEmpty
        var message: String = "",
        val tool: Boolean = false,
    )

    private val chatClientWithToolsBuilder: ChatClient.Builder = chatClient.mutate()
        .defaultTools(*tools)

    fun chatId(uid: Uid) = "chat-${uid}"

    @GetMapping("history")
    fun getHistory(
        @ShouldLogin token: Token,
    ): List<ChatMessageItem> {
        return chatMemory.get(chatId(token.user)).map {
            ChatMessageItem(it.messageType.name.lowercase(), it.text)
        }
    }

    @DeleteMapping("history")
    fun clearHistory(
        @ShouldLogin token: Token,
    ) {
        chatMemory.clear(chatId(token.user))
    }

    @SkipHandle
    @PostMapping("/chat")
    fun chat(
        @ShouldLogin token: Token,
        @RequestBody param: ChatParam,
        resp: HttpServletResponse
    ): Flux<String> {
        return chatClient.let {
            if (param.tool)
                chatClientWithToolsBuilder.build()
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