package com.yjl.hnas.fs

import java.io.File
import java.net.URI
import java.nio.file.*
import java.util.*

/**
 * `access:/path`
 *
 * - `/a` public/relative
 * - `123:/b` private
 *
 * @author YJL
 */
class VirtualPath private constructor(
    val fs: VirtualFilesystem,
    private val access: Long?,
    private val paths: Array<String>,
    private val absolute: Boolean
) : Path {

    companion object {
        val BAD_PATH_REGEX = Regex("[<>\\\\]")
        private fun toPaths(paths: Collection<String>): Array<String> = paths.toTypedArray()
        internal fun check(path: Path): VirtualPath {
            if (path !is VirtualPath)
                throw IllegalArgumentException("Not a VirtualPath")
            return path
        }

        /**
         * 复制并在前插入`count`个为父级（`..`）
         */
        private fun Array<String>.cloneInsertParent(count: Int) = Array(count + size) {
            if (it < count) ".." else this[it - count]
        }

        /**
         * 复制并设置前`count`个为父级（`..`）
         */
        private fun Array<String>.cloneSetParent(count: Int) = Array(count) {
            if (it < count) ".." else this[it]
        }
    }

    val path by lazy {
        val sb = StringBuilder()
        if (absolute)
            sb.append(VirtualFilesystem.SEPARATOR)
        paths.joinTo(sb, VirtualFilesystem.SEPARATOR)
            .toString()
    }
    val fullPath by lazy {
        if (access != null)
            StringBuilder(access.toString())
                .append(":")
                .append(path)
                .toString()
        else
            path
    }

    val isRoot: Boolean
        get() = absolute && paths.isEmpty()

    internal constructor(
        fs: VirtualFilesystem,
        access: Long?,
        abs: Boolean,
        vararg paths: String
    ) : this(fs, access, arrayOf(*paths), abs)

    internal constructor(
        fs: VirtualFilesystem,
        access: Long?,
        abs: Boolean,
        paths: Collection<String>,
    ) : this(fs, access, toPaths(paths), abs)

    val names: List<String> = paths.toList()

    override fun compareTo(other: Path): Int = compareTo(check(other))

    fun compareTo(o: VirtualPath): Int {
        if (access != o.access)
            throw IllegalArgumentException("Different access")
        return Arrays.compare(paths, o.paths)
    }

    override fun register(
        watcher: WatchService,
        events: Array<out WatchEvent.Kind<*>>,
        vararg modifiers: WatchEvent.Modifier?
    ): WatchKey {
        TODO("Not yet implemented")
    }

    override fun getFileSystem() = fs

    override fun isAbsolute() = absolute

    override fun getRoot() = clone(access, true, emptyArray())

    override fun getFileName() = emptyOr { clone(access, false, arrayOf(paths[paths.lastIndex])) }

    override fun getParent() = emptyOr { clone(access, absolute, paths.sliceArray(0..<paths.lastIndex)) }

    override fun getNameCount() = paths.size

    override fun getName(index: Int) = clone(access, false, arrayOf(paths[index]))

    override fun subpath(beginIndex: Int, endIndex: Int) =
        clone(access, false, paths.sliceArray(beginIndex until endIndex))

    override fun startsWith(other: Path) = startsWith(check(other))

    private fun canCompare(o: VirtualPath): Boolean {
        if (access == null) {
            if (o.access != null)
                return false
        } else {
            if (access != o.access)
                return false
        }

        return true
    }

    fun startsWith(o: VirtualPath): Boolean {
        if (!canCompare(o))
            return false

        if (paths.size < o.paths.size)
            return false
        for (i in paths.indices) {
            if (paths[i] != o.paths[i])
                return false
        }
        return true
    }

    override fun endsWith(other: Path) = endsWith(check(other))

    fun endsWith(o: VirtualPath): Boolean {
        if (!canCompare(o))
            return false
        val s = paths.size
        val ps = o.paths.size
        for (i in 1..ps) {
            if (paths[s - i] != o.paths[ps - i])
                return false
        }
        return true
    }

    override fun normalize(): VirtualPath = VirtualPath(fs, access, absolute, mutableListOf<String>().apply {
        for (n in paths) {
            when (n) {
                "." -> continue
                ".." -> removeLastOrNull()
                else -> add(n)
            }
        }
    })

    override fun resolve(other: String): VirtualPath {
        return resolve(fs.getPath(other))
    }

    override fun resolve(other: Path) = resolve(check(other))

    fun resolve(o: VirtualPath): VirtualPath = (
            if (o.isAbsolute || (o.access != null && access != o.access)) o
            else VirtualPath(fs, access, paths + o.paths, absolute)
            ).normalize()

    override fun relativize(other: Path): VirtualPath = relativize(check(other))

    fun relativize(o: VirtualPath): VirtualPath {
        if (access != o.access)
            return o.clone(access, true, emptyArray())
        // /a/b
        // /a/b     => .
        // /a/b/c/d => c/d
        // /a       => ../
        // /a/c     => ../c
        // /b       => ../../b
        // /b/c     => ../../b/c
        val s = paths.size
        val os = o.paths.size
        val len = kotlin.math.min(s, os)
        var sameLen = 0
        for (i in 0..<len) {
            if (paths[i] != o.paths[i])
                break
            sameLen++
        }
        if (os == s && sameLen == s)
            return clone(null, false, arrayOf("."))

        return o.clone(
            null, false, when (sameLen) {
                0 -> o.paths.cloneInsertParent(s)
                s -> o.paths.sliceArray(sameLen..<os)
                else -> o.paths.cloneSetParent(sameLen)
            }
        )
    }

    override fun toUri(): URI {
        // virtual://access/path
        return URI(
            VirtualFileSystemProvider.SCHEMA,
            access?.toString(),
            path,
            null
        )
    }

    override fun toAbsolutePath(): VirtualPath {
        return if (isAbsolute) this
        else clone(access, true, paths.clone())
    }

    override fun toRealPath(vararg options: LinkOption?) = this

    override fun toFile(): File {
        throw UnsupportedOperationException()
    }

    override fun toString() = fullPath

    override fun hashCode() = fullPath.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other !is VirtualPath || javaClass != other.javaClass || fs !== other.fs)
            return false
        if (other === this)
            return true
        return access == other.access && paths.contentEquals(other.paths)
    }

    infix fun same(o: VirtualPath): Boolean {
        return access == o.access && paths.contentEquals(o.paths)
    }

    fun user() = access

    fun isPublic() = access == null

    operator fun plus(path: String) = resolve(path)

    operator fun plus(path: VirtualPath) = resolve(path)

    private fun emptyOr(access: Long? = null, block: () -> VirtualPath) =
        if (paths.isEmpty()) VirtualPath(fs, access, false) else block()

    private fun clone(access: Long?, abs: Boolean, paths: Array<String>) = VirtualPath(fs, access, abs, *paths)
}
