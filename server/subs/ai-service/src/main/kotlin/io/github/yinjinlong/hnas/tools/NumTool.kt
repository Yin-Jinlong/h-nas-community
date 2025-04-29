package io.github.yinjinlong.hnas.tools

import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * @author YJL
 */
@Service
class NumTool {

    enum class OP {
        ADD {
            override fun calc(a: BigDecimal, b: BigDecimal, precision: Int): BigDecimal =
                a.add(b)
        },
        SUB {
            override fun calc(a: BigDecimal, b: BigDecimal, precision: Int): BigDecimal =
                a.subtract(b)
        },
        MUL {
            override fun calc(a: BigDecimal, b: BigDecimal, precision: Int): BigDecimal =
                a.multiply(b)
        },
        DIV {
            override fun calc(a: BigDecimal, b: BigDecimal, precision: Int): BigDecimal =
                a.divide(b, precision, RoundingMode.HALF_DOWN)
        };

        abstract fun calc(a: BigDecimal, b: BigDecimal, precision: Int): BigDecimal
    }

    @Tool(description = "开方")
    fun numSqrt(
        @ToolParam(description = "十进制小数") a: String,
        @ToolParam(description = "精度") precision: Int,
    ): String {
        return BigDecimal(a).sqrt(MathContext(precision, RoundingMode.HALF_DOWN)).toPlainString()
    }

    @Tool(description = "简单四则运算")
    fun numCalc(
        @ToolParam(description = "第一个数，十进制小数") a: String,
        @ToolParam(description = "第二个数，十进制小数") b: String,
        @ToolParam(description = "操作，包括加减乘除") op: OP,
        @ToolParam(required = false, description = "精度，默认为2，在进行除法时用到，其它操作时忽略") precision: Int = 2,
    ): String {
        return op.calc(BigDecimal(a), BigDecimal(b), precision).toPlainString()
    }

}