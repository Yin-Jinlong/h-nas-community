package com.yjl.hnas.service.virtual

import com.yjl.hnas.entity.Uid
import java.net.URI
import java.nio.file.*
import java.util.*

/**
 * @author YJL
 */
class VirtualPath private constructor(
    private val fs: VirtualFileSystem,
    val user: Uid?,
    private val paths: List<String>
) : Path, Cloneable {

    internal constructor(fs: VirtualFileSystem, user: Uid?, path: String) : this(fs, user, path.let {
        if (path.contains("//"))
            throw IllegalArgumentException("path could not contain //")
        path.replace("\\", "/").split(VirtualFileSystem.SEPARATOR)
    })

    private fun check(path: Path): VirtualPath {
        if (path !is VirtualPath)
            throw ProviderMismatchException()
        if (path.user != null && path.user != user)
            throw IllegalArgumentException("Path's user '${path.user}' does not match: $user")
        return path
    }

    private fun copy(paths: List<String> = this.paths, from: Int = 0, to: Int = paths.size) =
        VirtualPath(fs, user, paths.subList(from, to))

    override fun compareTo(other: Path): Int {
        val path = check(other)
        return toAbsolutePath().paths.zip(path.toAbsolutePath().paths)
            .map { it.first.compareTo(it.second) }
            .firstOrNull { it != 0 } ?: 0
    }

    override fun register(
        watcher: WatchService,
        events: Array<out WatchEvent.Kind<*>>,
        vararg modifiers: WatchEvent.Modifier?
    ): WatchKey {
        throw UnsupportedOperationException()
    }

    override fun getFileSystem() = fs

    override fun isAbsolute() = paths.size > 1 && paths[0].isEmpty()

    override fun getRoot() = VirtualPath(fs, user, listOf("", ""))

    override fun getFileName(): VirtualPath {
        return copy(
            listOf(
                if (paths.isEmpty()) ""
                else paths.last()
            )
        )
    }

    override fun getParent(): VirtualPath {
        return resolve("..").toAbsolutePath()
    }

    override fun getNameCount() = kotlin.math.min(0, paths.size - 1)

    override fun getName(index: Int) = VirtualPath(fs, user, paths[index + 1])

    override fun subpath(beginIndex: Int, endIndex: Int) = copy(paths, beginIndex, endIndex)

    override fun startsWith(other: Path): Boolean {
        val path = check(other)
        val a = toAbsolutePath().paths
        val b = path.toAbsolutePath().paths
        return b.size <= a.size && b.zip(a).all { it.first == it.second }
    }

    override fun endsWith(other: Path): Boolean {
        val path = check(other)
        val a = toAbsolutePath().paths.reversed()
        val b = path.toAbsolutePath().paths.reversed()
        return b.size <= a.size && b.zip(a).all { it.first == it.second }
    }

    override fun normalize(): VirtualPath {
        val list = Stack<String>()
        for (p in paths) {
            when (p) {
                "" -> list.clear()
                "." -> continue
                ".." -> if (list.isNotEmpty()) list.pop()
                else -> list.push(p)
            }
            if (list.isEmpty())
                list.add("")
        }
        return copy(list)
    }

    override fun resolve(other: Path): VirtualPath {
        val path = check(other).normalize()
        if (path.isAbsolute)
            return path.clone()
        return copy(paths + path.paths)
    }

    override fun resolve(path: String): VirtualPath {
        val ps = path.split("/")
        return copy(paths + ps).normalize()
    }

    override fun relativize(other: Path): Path {
        TODO("Not yet implemented")
    }

    override fun toUri(): URI {
        return URI(
            VirtualFileSystemProvider.SCHEME,
            user.toString(), null, 0,
            paths.joinToString("/"),
            null, null
        )
    }

    override fun toAbsolutePath(): VirtualPath {
        val p = normalize()
        if (p.isAbsolute)
            return p
        val ps = p.paths
        return p.copy(ps.toMutableList().apply {
            add(0, "")
        })
    }

    override fun toRealPath(vararg options: LinkOption?): VirtualPath {
        return toAbsolutePath()
    }

    override fun toFile(): VirtualFile {
        return VirtualFile(toAbsolutePath())
    }

    public override fun clone(): VirtualPath {
        return copy()
    }

    override fun toString(): String {
        return paths.joinToString("/")
    }

    fun toFullString() = "${user ?: ""}:${toString()}"

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other !is VirtualPath)
            return false

        return fs == other.fs &&
                user == other.user &&
                paths == other.paths
    }

    override fun hashCode(): Int {
        return Objects.hash(fs, user, paths)
    }


}