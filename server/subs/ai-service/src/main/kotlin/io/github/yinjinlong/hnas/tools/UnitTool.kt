package io.github.yinjinlong.hnas.tools

import io.github.yinjinlong.hnas.tools.FileTool.Companion.NUM_1024
import io.github.yinjinlong.hnas.tools.FileTool.Companion.SIZE_UNITS
import io.github.yinjinlong.hnas.utils.logger
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author YJL
 */
@ToolService
class UnitTool : CommonTool(UnitTool::class.logger()) {

    @Tool(description = "文件大小转换成人性化字符串")
    fun fileSizeToHumanStr(
        @ToolParam(description = "文件大小")
        size: String,
        @ToolParam(description = "保留小数位，默认2", required = false)
        precision: Int = 2,
    ): String {
        logCall(size, precision)
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