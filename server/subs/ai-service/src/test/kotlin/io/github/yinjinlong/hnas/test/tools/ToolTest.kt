package io.github.yinjinlong.hnas.test.tools

import io.github.yinjinlong.hnas.tools.AbstractTool
import io.github.yinjinlong.spring.boot.util.getLogger
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author YJL
 */
class ToolTest {

    class Tool : AbstractTool(Tool::class.getLogger()) {

        fun test(): String {
            return callerMethod
        }
    }

    @Test
    fun testCaller() {
        assertEquals("testCaller", Tool().test())
    }

}
