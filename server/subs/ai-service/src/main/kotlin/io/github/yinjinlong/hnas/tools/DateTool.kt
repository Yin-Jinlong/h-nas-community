package io.github.yinjinlong.hnas.tools

import com.nlf.calendar.Lunar
import io.github.yinjinlong.spring.boot.util.getLogger
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam

/**
 * @author YJL
 */
@ToolService
class DateTool : CommonTool(DateTool::class.getLogger()) {

    companion object {
        val weekNames = arrayOf("Sun.", "Mon.", "Tue.", "Wed.", "Thu.", "Fri.", "Sat.")

        private fun dateStr(year: String, month: String, day: String, dayExt: String = "日") =
            "${year}年${month}月$day$dayExt"

        private val Lunar.gongYuanDate: String
            get() = dateStr(yearInChinese, monthInChinese, dayInChinese, "")

        private val Lunar.ganZhiDate: String
            get() = dateStr(yearInGanZhi, monthInGanZhi, dayInGanZhi)

        private val Lunar.shengXiaoDate: String
            get() = dateStr(yearShengXiao, monthShengXiao, dayShengXiao)
    }

    @Tool(description = "获取当前日期字符串，格式： 公历(年月日) 中国农历(年月日) 农历额外信息")
    fun getCurrentDate(
        @ToolParam(description = "添加干支表示，默认是", required = false)
        withGanZhi: Boolean = true,
        @ToolParam(description = "添加生肖表示，默认是", required = false)
        withShengXiao: Boolean = true,
    ): String {
        logCall(withGanZhi, withShengXiao)
        val lunar = Lunar()
        val solar = lunar.solar
        val solarStr = "${solar.year}/${solar.month}/${solar.day} ${weekNames[solar.week]}"
        return StringBuilder(64).apply {
            append(solarStr)
            append(" ")
            append(lunar.gongYuanDate)
            if (withGanZhi) {
                append(" ")
                append(lunar.ganZhiDate)
            }
            if (withShengXiao) {
                append(" ")
                append(lunar.shengXiaoDate)
            }
            append(" ")
            append("星期${lunar.weekInChinese}")
            lunar.jieQi?.let {
                append(" ")
                append(it)
            }
        }.toString()
    }

    @Tool(description = "获取当前农历时间全部信息，包括日期，节气，黄历等")
    fun getCurrentLunarTimeFullInfo(): String {
        logCall()
        return Lunar().toFullString()
    }
}
