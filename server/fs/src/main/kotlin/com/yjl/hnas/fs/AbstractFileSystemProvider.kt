package com.yjl.hnas.fs

import java.net.URI
import java.nio.channels.SeekableByteChannel
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import java.nio.file.spi.FileSystemProvider

/**
 * @author YJL
 */
abstract class AbstractFileSystemProvider<
        PM : PathManager<P>,
        FSP : AbstractFileSystemProvider<PM, FSP, FS, P>,
        FS : AbstractFileSystem<FSP, FS, P>,
        P : AbstractPath<FSP, FS, P>
        >(
    val manager: PM
) : FileSystemProvider() {

    fun check(path: Path): P {
        val fs = path.fileSystem as? AbstractFileSystem<*, *, *>?
            ?: throw IllegalArgumentException("path is not a AbstractPath")
        return fs.check(path) as P
    }

    fun checkScheme(uri: URI) {
        if (uri.scheme.lowercase() != scheme)
            throw IllegalArgumentException("uri scheme is not $scheme")
    }

    abstract override fun newFileSystem(uri: URI, env: MutableMap<String, *>): FS

    override fun getFileSystem(uri: URI): FS = newFileSystem(uri, mutableMapOf<String, Any>())

    override fun getPath(uri: URI): P = getFileSystem(uri).getPath(uri.path)

    override fun newByteChannel(
        path: Path,
        options: MutableSet<out OpenOption>,
        vararg attrs: FileAttribute<*>
    ): SeekableByteChannel {
        throw UnsupportedOperationException()
    }

    override fun newDirectoryStream(dir: Path, filter: DirectoryStream.Filter<in Path>): DirectoryStream<Path> {
        throw UnsupportedOperationException()
    }

    override fun createDirectory(dir: Path, vararg attrs: FileAttribute<*>) {
        throw UnsupportedOperationException()
    }

    override fun delete(path: Path) {
        throw UnsupportedOperationException()
    }

    override fun copy(source: Path, target: Path, vararg options: CopyOption) {
        throw UnsupportedOperationException()
    }

    override fun move(source: Path, target: Path, vararg options: CopyOption) {
        throw UnsupportedOperationException()
    }

    abstract fun isSameFile(path1: P, path2: P): Boolean

    override fun isSameFile(path: Path, path2: Path): Boolean {
        return isSameFile(check(path), check(path2))
    }

    override fun isHidden(path: Path): Boolean {
        throw UnsupportedOperationException()
    }

    override fun getFileStore(path: Path): FileStore {
        throw UnsupportedOperationException()
    }

    override fun checkAccess(path: Path, vararg modes: AccessMode) {
        manager.checkAccess(check(path), *modes)
    }

    override fun <V : FileAttributeView?> getFileAttributeView(
        path: Path?,
        type: Class<V>?,
        vararg options: LinkOption?
    ): V {
        throw UnsupportedOperationException()
    }

    override fun <A : BasicFileAttributes?> readAttributes(
        path: Path,
        type: Class<A>,
        vararg options: LinkOption
    ): A {
        throw UnsupportedOperationException()
    }

    override fun readAttributes(
        path: Path,
        attributes: String,
        vararg options: LinkOption
    ): MutableMap<String, Any> {
        throw UnsupportedOperationException()
    }

    override fun setAttribute(path: Path, attribute: String, value: Any, vararg options: LinkOption) {
        throw UnsupportedOperationException()
    }
}
