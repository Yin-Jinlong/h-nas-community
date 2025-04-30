package io.github.yinjinlong.hnas.test.tools

import io.github.yinjinlong.hnas.token.Token
import io.github.yinjinlong.hnas.tools.FileTool
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.web.client.RestTemplate
import kotlin.test.Test


/**
 * @author YJL
 */
class FileToolTest {

    @Test
    fun testFileSizeToHumanStr() {
        val tool = FileTool(RestTemplate(), Token(0, ""))
        assertEquals("0.00B", tool.fileSizeToHumanStr("0"))
        assertEquals("1.00KB", tool.fileSizeToHumanStr("1024"))
        assertEquals("0.95KB", tool.fileSizeToHumanStr("973"))
        assertEquals("3.60MB", tool.fileSizeToHumanStr("3774874"))
        assertEquals("395.70MB", tool.fileSizeToHumanStr("414918177"))
    }

}