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
import java.math.RoundingMode

/**
 * @author YJL
 */
class FileTool(
    private val restTemplate: RestTemplate,
    private val token: Token,
) {

    companion object {
        const val HOST = "file-service"
        val SIZE_UNITS = arrayOf("", "K", "M", "G", "T")
        val NUM_1024 = BigDecimal(1024)
    }

    val logger = getLogger()

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
        logger.info("${token.user} call getFiles => $path $private")
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
        logger.info("${token.user} call getFileInfo => $path $private")
        return restTemplate.get<Any>(
            API.http(
                HOST, "${API.FILE}/info",
                baseParam(path, private)
            ), headers = mapOf(
                HttpHeaders.AUTHORIZATION to token.token
            )
        ).body
    }

    @Tool(description = "文件大小转换成人性化字符串")
    fun fileSizeToHumanStr(
        @ToolParam(description = "文件大小")
        size: String,
        @ToolParam(description = "保留小数位，默认2", required = false)
        precision: Int = 2,
    ): String {
        logger.info("${token.user} call fileSizeToHumanStr => $size $precision")
        val scale = precision * 2
        var num = BigDecimal(size)
        val factor = BigDecimal("0.9")
        var ui = 0
        do {
            val r = num.divide(NUM_1024, scale, RoundingMode.HALF_DOWN)
            if (r >= factor) {
                num = r
                ui++
            } else
                break
        } while (true)
        return "${num.setScale(precision, RoundingMode.HALF_DOWN)}${SIZE_UNITS[ui]}B"
    }
}
