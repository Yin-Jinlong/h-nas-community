package com.yjl.hnas.fs.test

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.fs.*
import java.nio.file.AccessMode
import kotlin.io.path.pathString
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 * @author YJL
 */
class PubPathTest {

    val provider = PubFileSystemProvider(object : PubPathManager {
        override fun checkAccess(path: PubPath, vararg modes: AccessMode) {

        }

        override fun toVirtualPath(path: PubPath): VirtualPath {
            TODO("Not yet implemented")
        }

        override fun deleteFile(path: PubPath) {

        }

        override fun fileExists(path: PubPath): Boolean {
            return true
        }

        override fun createFolder(path: PubPath, owner: Uid) {

        }
    })

    val fs = provider.getFileSystem()

    val path = fs.getPath("a/b/c")
    val pathAbs = fs.getPath("/a/b/c")

    @Test
    fun testAbsolute() {
        val abs = path.toAbsolutePath()
        assertEquals("/a/b/c", abs.pathString)
    }

    @Test
    fun testPathString() {
        assertEquals("a/b/c", path.pathString)
    }

    @Test
    fun testParent() {
        var parent = path.parent
        assertEquals("a/b", parent.pathString)
        parent = parent.parent
        assertEquals("a", parent.pathString)
        parent = parent.parent
        assertEquals("", parent.pathString)
        parent = parent.parent
        assertEquals("", parent.pathString)

        parent = pathAbs.parent
        assertEquals("/a/b", parent.pathString)
        parent = parent.parent
        assertEquals("/a", parent.pathString)
        parent = parent.parent
        assertEquals("/", parent.pathString)
        parent = parent.parent
        assertEquals("/", parent.pathString)
    }

    @Test
    fun testNormalize() {
        val path = fs.getPath("a/b/c/./../d")
        assertEquals("a/b/d", path.normalize().pathString)

        val pathAbs = fs.getPath("/a/b/c/./../d")
        assertEquals("/a/b/d", pathAbs.normalize().pathString)

        val pathRoot = fs.getPath("a/b/c//v")
        assertEquals("/v", pathRoot.normalize().pathString)
        val pathRootAbs = fs.getPath("/a/b/c//v")
        assertEquals("/v", pathRootAbs.normalize().pathString)
    }

    @Test
    fun testResolve() {
        val p1 = path.resolve("d")
        assertEquals("a/b/c/d", p1.pathString)
        val p2 = path.resolve("/d")
        assertEquals("/d", p2.pathString)
        val p3 = path.resolve("..")
        assertEquals("a/b", p3.pathString)

        val p4 = pathAbs.resolve("d")
        assertEquals("/a/b/c/d", p4.pathString)
        val p5 = pathAbs.resolve("/d")
        assertEquals("/d", p5.pathString)
        val p6 = pathAbs.resolve("..")
        assertEquals("/a/b", p6.pathString)
    }
}