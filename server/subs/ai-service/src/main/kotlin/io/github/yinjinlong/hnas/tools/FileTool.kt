package io.github.yinjinlong.hnas.tools

import io.github.yinjinlong.hnas.controller.API
import io.github.yinjinlong.hnas.token.Token
import io.github.yinjinlong.hnas.utils.get
import io.github.yinjinlong.spring.boot.util.getLogger
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal

/**
 * @author YJL
 */
class FileTool(
    restTemplate: RestTemplate,
    token: Token,
) : UserTool(FileTool::class.getLogger(), restTemplate, token) {

    companion object {
        const val HOST = "file-service"
        val SIZE_UNITS = arrayOf("", "K", "M", "G", "T")
        val NUM_1024 = BigDecimal(1024)
    }

    private fun baseParam(path: String, private: Boolean) = mapOf(
        "path" to path,
        "private" to private
    )

    @Tool(description = "获取目录下文件，包含一个响应实体")
    fun getFiles(
        @ToolParam(description = "文件路径")
        path: String,
        @ToolParam(description = "是否为私有文件, 默认为否", required = false)
        private: Boolean = false
    ): Map<*, *>? {
        logCall(token.user, path, private)
        return restTemplate.get<Any>(
            API.http(
                HOST, "${API.FILE}/files",
                baseParam(path, private)
            ), headers = mapOf(
                HttpHeaders.AUTHORIZATION to token.token
            )
        ).body
    }

    @Tool(description = "获取文件信息，包含一个响应实体")
    fun getFileInfo(
        @ToolParam(description = "文件路径")
        path: String,
        @ToolParam(description = "是否为私有文件, 默认为否", required = false)
        private: Boolean = false
    ): Map<*, *>? {
        logCall(token.user, path, private)
        return restTemplate.get<Any>(
            API.http(
                HOST, "${API.FILE}/info",
                baseParam(path, private)
            ), headers = mapOf(
                HttpHeaders.AUTHORIZATION to token.token
            )
        ).body
    }
}
