package com.yjl.hnas.service.test

import java.nio.file.spi.FileSystemProvider
import kotlin.test.Test

/**
 * @author YJL
 */
class VirtualFileTest {

    @Test
    fun testPath() {
        println("/".split("/"))
        println("".split("/"))
    }

    @Test
    fun test() {
        FileSystemProvider.installedProviders().forEach {
            println(it.scheme)
        }
    }

}