package io.github.yinjinlong.hnas.test.tools

import io.github.yinjinlong.hnas.tools.DateTool
import kotlin.test.Test

/**
 * @author YJL
 */
class DateToolTest {

    val tool=DateTool()

    @Test
    fun test(){
        println(tool.getCurrentDate())
    }

}