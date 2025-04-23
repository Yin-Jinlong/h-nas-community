package io.github.yinjinlong.hnas.data.test

import io.github.yinjinlong.hnas.data.FileRange
import org.junit.jupiter.api.Assertions
import kotlin.test.Test

/**
 * @author YJL
 */
class FileRangeTest {

    val r1 = FileRange(0, 10)

    @Test
    fun testAdd() {
        Assertions.assertEquals(FileRange(0, 15), r1 + FileRange(5, 15))
        Assertions.assertEquals(FileRange(0, 15), r1 + FileRange(10, 15))
        Assertions.assertEquals(FileRange(0, 11), r1 + FileRange(10, 11))
        Assertions.assertEquals(r1, r1 + FileRange(5, 9))
        Assertions.assertEquals(null, r1 + FileRange(11, 12))
    }


    @Test
    fun testSub() {
        Assertions.assertArrayEquals(arrayOf(FileRange(0, 3), FileRange(7, 10)), (r1 - FileRange(3, 7)).toTypedArray())
        Assertions.assertArrayEquals(
            arrayOf(FileRange(0, 1), FileRange(3, 4), FileRange(7, 10)),
            (r1 - listOf(
                FileRange(1, 3),
                FileRange(4, 7),
            )).toTypedArray()
        )
    }
}