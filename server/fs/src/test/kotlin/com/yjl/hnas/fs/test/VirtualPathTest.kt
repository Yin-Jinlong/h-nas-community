package com.yjl.hnas.fs.test

import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.fs.VirtualPath
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 * @author YJL
 */
class VirtualPathTest {
    val fsp = VirtualFileSystemProvider(VirtualFileManagerImpl())
    val fs = fsp.virtualFilesystem
    val root = fs.getPubPath("/a/b/c").toAbsolutePath()

    val ROOT_PATH = "/a/b/c"
    val ROOT_FULL_PATH = "/a/b/c"

    private fun assertPath(exp: String, ps: Iterable<VirtualPath>) {
        for (p in ps)
            assertEquals(exp, p.path)
    }

    private fun assertFull(exp: String, ps: Iterable<VirtualPath>) {
        for (p in ps)
            assertEquals(exp, p.fullPath)
    }

    private fun assertPath(exp: String, vararg ps: VirtualPath) = assertPath(exp, ps.asIterable())
    private fun assertFull(exp: String, vararg ps: VirtualPath) = assertFull(exp, ps.asIterable())

    fun getPath(first: String, vararg more: String) = fs.getPath(first, more)

    @Test
    fun testGet() {
        val rootWithEnd = getPath("/a/b/c/")
        val rootCat = getPath("/a/b", "c")
        val rootCat2 = getPath("/a", "b/c")
        val rootCat3 = getPath("/a", "b", "c")
        val rootCatWithRoot = getPath("/x/y", "/a/b", "c")

        val roots = listOf(
            root,
            rootWithEnd,
            rootCat,
            rootCat2,
            rootCat3,
            rootCatWithRoot
        )

        assertPath(ROOT_PATH, roots)
        assertFull(ROOT_FULL_PATH, roots)
    }

    @Test
    fun testNormalize() {
        val r1 = getPath("/c/../../..", "a", "b/./c").normalize()
        assertPath(ROOT_PATH, r1)
        assertFull(ROOT_FULL_PATH, r1)
    }

    @Test
    fun testResolve() {
        val p1 = root.resolve("d")
        assertFull("/a/b/c/d", p1)
    }

    @Test
    fun testParent() {
        val p = root.parent
        assertPath("/a/b", p)
        assertFull("/a/b", p)
    }
}