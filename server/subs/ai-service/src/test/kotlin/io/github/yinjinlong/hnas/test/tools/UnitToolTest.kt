package io.github.yinjinlong.hnas.test.tools

import io.github.yinjinlong.hnas.tools.UnitTool
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test


/**
 * @author YJL
 */
class UnitToolTest {

    @Test
    fun testFileSizeToHumanStr() {
        val tool = UnitTool()
        assertEquals("0.00B", tool.fileSizeToHumanStr("0"))
        assertEquals("1.00KB", tool.fileSizeToHumanStr("1024"))
        assertEquals("0.95KB", tool.fileSizeToHumanStr("973"))
        assertEquals("3.60MB", tool.fileSizeToHumanStr("3774874"))
        assertEquals("395.70MB", tool.fileSizeToHumanStr("414918177"))
    }

}