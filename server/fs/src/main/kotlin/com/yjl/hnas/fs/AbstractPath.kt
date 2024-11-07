package com.yjl.hnas.fs

import java.net.URI
import java.nio.file.*
import java.util.*

/**
 * @author YJL
 */
abstract class AbstractPath<
        FSP : AbstractFileSystemProvider<*, FSP, FS, P>,
        FS : AbstractFileSystem<FSP, FS, P>,
        P : AbstractPath<FSP, FS, P>
        >(
    protected val fs: FS,
    path: String,
) : Path, Cloneable {

    private val absolute: Boolean = path.startsWith("/")

    val path = path.let {
        if (path.endsWith("/"))
            it.substring(0, it.length - 1)
        else
            it
    }.let {
        if (it.startsWith("/"))
            it.substring(1)
        else
            it
    }

    private val paths = this.path.split("/")

    protected abstract fun clone(path: String, absolute: Boolean): P

    override fun compareTo(other: Path): Int {
        val path = fs.check(other)
        return path.path.compareTo(this.path)
    }

    override fun register(
        watcher: WatchService,
        events: Array<out WatchEvent.Kind<*>>,
        vararg modifiers: WatchEvent.Modifier?
    ): WatchKey {
        throw UnsupportedOperationException()
    }

    override fun getFileSystem() = fs

    override fun isAbsolute() = absolute

    override fun getRoot(): P {
        return clone("/", true)
    }

    override fun getFileName(): P {
        return clone(paths.last(), false)
    }

    override fun getParent(): P {
        if (nameCount <= 1)
            return root
        val ps = toAbsolutePath().paths.dropLast(1)
        return clone(ps.joinToString("/"), true)
    }

    override fun getNameCount(): Int {
        return paths.size
    }

    override fun getName(index: Int): P {
        return clone(paths[index], false)
    }

    override fun subpath(beginIndex: Int, endIndex: Int): P {
        if (beginIndex !in paths.indices || endIndex !in 0..paths.size)
            throw IllegalArgumentException("Invalid index")
        return clone(paths.subList(beginIndex, endIndex).joinToString("/"), false)
    }

    override fun startsWith(other: Path): Boolean {
        val p = fs.check(other)
        return toAbsolutePath().path.startsWith(p.toAbsolutePath().path)
    }

    override fun endsWith(other: Path): Boolean {
        val p = fs.check(other)
        return toAbsolutePath().path.endsWith(p.toAbsolutePath().path)
    }

    override fun normalize(): P {
        val p = path.substringAfterLast("//")
        val ps = p.split("/")
        val r = mutableListOf<String>()
        ps.forEach {
            when (it) {
                ".." -> r.removeLastOrNull()
                "." -> {}
                else -> r.add(it)
            }
        }
        return clone(r.joinToString("/"), true)
    }

    override fun resolve(other: String): P {
        return resolve(clone(other, false))
    }

    override fun resolve(other: Path): P {
        val p = fs.check(other)
        return clone("$path/${p.path}", true).normalize()
    }

    override fun relativize(other: Path): P {
        val p = fs.check(other)
        fs.check(p)
        if (!p.startsWith(this))
            return root
        val s = p.toAbsolutePath().path.substring(toAbsolutePath().path.length)
        return clone(s.let {
            if (it.startsWith("/"))
                it.substring(1)
            else
                it
        }, false)
    }

    override fun toUri(): URI {
        throw UnsupportedOperationException()
    }

    override fun toAbsolutePath(): P {
        if (isAbsolute)
            return clone()
        val p = normalize()
        return p.clone(p.path, true)
    }

    override fun toRealPath(vararg options: LinkOption?): P {
        return clone()
    }

    override fun toString(): String {
        val p = if (isAbsolute) "/$path" else path
        return p
    }

    override fun clone(): P {
        return clone(path, absolute)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other !is AbstractPath<*, *, *>)
            return false
        if (javaClass != other.javaClass)
            return false

        return fs === other.fs &&
                path == other.path
    }

    override fun hashCode(): Int {
        return Objects.hash(fs, path)
    }
}