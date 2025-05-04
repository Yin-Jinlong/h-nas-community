package io.github.yinjinlong.hnas.tools

import io.github.yinjinlong.hnas.utils.logger
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author YJL
 */
@ToolService
class TimeTool : CommonTool(TimeTool::class.logger()) {

    companion object {
        /**
         * 年-月-日 时:分:秒 时区
         */
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss X", Locale.CHINA)

        /**
         * 年-月-日 时:分:秒.毫秒 时区
         */
        val dateTimeWithMillisecondFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS X", Locale.CHINA)
    }

    @Tool(description = "获取当前时间字符串")
    fun currentTime(
        @ToolParam(description = "是否带毫秒")
        withMillisecond: Boolean,
    ): String {
        logCall(withMillisecond)
        return if (withMillisecond) {
            dateTimeWithMillisecondFormat.format(Date())
        } else {
            dateTimeFormat.format(Date())
        }
    }

}