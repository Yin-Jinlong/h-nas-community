package io.github.yinjinlong.hnas.test.tools

import io.github.yinjinlong.hnas.tools.AbstractTool
import io.github.yinjinlong.hnas.utils.logger
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author YJL
 */
class ToolTest {

    class Tool : AbstractTool(Tool::class.logger()) {

        fun test(): String {
            return callerMethod
        }
    }

    @Test
    fun testCaller() {
        assertEquals("testCaller", Tool().test())
    }

}
