package com.yjl.hnas.data.test

import com.yjl.hnas.data.FileRange
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

/**
 * @author YJL
 */
class FileRangeTest {

    val r1 = FileRange(0, 10)

    @Test
    fun testAdd() {
        assertEquals(FileRange(0, 15), r1 + FileRange(5, 15))
        assertEquals(FileRange(0, 15), r1 + FileRange(10, 15))
        assertEquals(FileRange(0, 11), r1 + FileRange(10, 11))
        assertEquals(r1, r1 + FileRange(5, 9))
        assertEquals(null, r1 + FileRange(11, 12))
    }


    @Test
    fun testSub() {
        assertArrayEquals(arrayOf(FileRange(0, 3), FileRange(7, 10)), (r1 - FileRange(3, 7)).toTypedArray())
        assertArrayEquals(
            arrayOf(FileRange(0, 1), FileRange(3, 4), FileRange(7, 10)),
            (r1 - listOf(
                FileRange(1, 3),
                FileRange(4, 7),
            )).toTypedArray()
        )
    }
}