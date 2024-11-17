package com.yjl.hnas.fs

import java.net.URI
import java.nio.file.*
import java.nio.file.attribute.FileAttribute
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

    val bundleAttrs = mutableMapOf<String, FileAttribute<*>>()

    private val absolute: Boolean = path.startsWith("/")
    private val prefix = if (absolute) "/" else ""

    /**
     * 不带首个/
     */
    val path = path.let {
        if (path.endsWith("/"))
            it.substring(0, it.length - 1)
        else
            it
    }.let {
        if (it.startsWith("/"))
            it.replace(Regex("^/"), "")
        else
            it
    }

    /**
     * 完整路径
     */
    val fullPath = prefix + this.path

    private val paths = this.path.split("/")

    protected abstract fun clone(path: String): P

    override fun compareTo(other: Path): Int {
        val path = fs.check(other)
        return path.fullPath.compareTo(this.path)
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
        return clone("/")
    }

    override fun getFileName(): P {
        return clone(paths.last())
    }

    override fun getParent(): P {
        if (nameCount <= 1)
            return if (absolute) root else clone("")
        val ps = paths.dropLast(1)
        return clone(ps.joinToString("/", prefix = prefix))
    }

    override fun getNameCount(): Int {
        return paths.size
    }

    override fun getName(index: Int): P {
        return clone(paths[index])
    }

    override fun subpath(beginIndex: Int, endIndex: Int): P {
        if (beginIndex !in paths.indices || endIndex !in 0..paths.size)
            throw IllegalArgumentException("Invalid index")
        return clone(paths.subList(beginIndex, endIndex).joinToString("/", prefix = prefix))
    }

    override fun startsWith(other: Path): Boolean {
        val p = fs.check(other)
        return toAbsolutePath().fullPath.startsWith(p.toAbsolutePath().fullPath)
    }

    override fun endsWith(other: Path): Boolean {
        val p = fs.check(other)
        return toAbsolutePath().fullPath.endsWith(p.toAbsolutePath().fullPath)
    }

    override fun normalize(): P {
        val ri = fullPath.lastIndexOf("//")
        var prefix = this.prefix
        val p = if (ri >= 0) fullPath.substring(ri + 2).also {
            prefix = "/"
        } else path
        val ps = p.split("/")
        val r = mutableListOf<String>()
        ps.forEach {
            when (it) {
                ".." -> r.removeLastOrNull()
                "." -> {}
                else -> r.add(it)
            }
        }
        return clone(r.joinToString("/", prefix = prefix))
    }

    override fun resolve(other: String): P {
        return resolve(fs.getPath(other))
    }

    override fun resolve(other: Path): P {
        val p = fs.check(other)
        return clone("$fullPath/${p.fullPath}").normalize()
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
        })
    }

    override fun toUri(): URI {
        throw UnsupportedOperationException()
    }

    override fun toAbsolutePath(): P {
        if (isAbsolute)
            return clone()
        val p = normalize()
        return p.clone("/$path")
    }

    override fun toRealPath(vararg options: LinkOption?): P {
        return toAbsolutePath()
    }

    fun isRoot() = path.isEmpty()

    override fun toString() = fullPath

    override fun clone(): P {
        return clone(fullPath)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other !is AbstractPath<*, *, *>)
            return false
        if (javaClass != other.javaClass)
            return false

        return fs === other.fs &&
                fullPath == other.fullPath
    }

    override fun hashCode(): Int {
        return Objects.hash(fs, path)
    }
}