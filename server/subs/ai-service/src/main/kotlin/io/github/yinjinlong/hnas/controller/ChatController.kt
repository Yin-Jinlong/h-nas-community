package io.github.yinjinlong.hnas.controller

import io.github.yinjinlong.hnas.annotation.ShouldLogin
import io.github.yinjinlong.hnas.data.ChatMessageItem
import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.token.Token
import io.github.yinjinlong.hnas.tools.NumTool
import io.github.yinjinlong.hnas.tools.TimeTool
import io.github.yinjinlong.spring.boot.annotations.SkipHandle
import jakarta.servlet.http.HttpServletResponse
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.context.ApplicationContext
import org.springframework.util.ResourceUtils
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

/**
 * @author YJL
 */
@RestController
@RequestMapping(API.AI)
class ChatController(
    model: OllamaChatModel,
    timeTool: TimeTool,
    numTool: NumTool,
    context: ApplicationContext,
) {
    val chatMemory = MessageWindowChatMemory.builder().build()

    val chatClient = ChatClient.builder(model)
        .defaultAdvisors {
            it.advisors(MessageChatMemoryAdvisor(chatMemory))
        }.defaultTools(timeTool, numTool)
        .defaultSystem(context.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "ai-system.md"))
        .build()

    data class ChatParam(
        var message: String = ""
    )

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
        return chatClient
            .prompt()
            .user(param.message)
            .advisors {
                it.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId(token.user))
            }
            .stream()
            .content()
    }

}