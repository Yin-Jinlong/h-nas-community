package com.yjl.hnas.service.virtual

import com.yjl.hnas.entity.Uid
import java.net.URI
import java.nio.channels.SeekableByteChannel
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import java.nio.file.spi.FileSystemProvider
import kotlin.io.path.absolute

/**
 *
 * @author YJL
 */
class VirtualFileSystemProvider : FileSystemProvider() {

    companion object {
        const val SCHEME = "vfile"
    }

    private fun URI.getUser(): Uid? {
        return if (userInfo == null || userInfo.isEmpty()) null
        else userInfo.toLongOrNull()
            ?: throw IllegalArgumentException("user is not number")
    }

    override fun getScheme() = SCHEME

    override fun newFileSystem(uri: URI?, env: MutableMap<String, *>?): VirtualFileSystem {
        if (uri != null && uri.scheme.lowercase() != SCHEME)
            throw IllegalArgumentException("URI scheme is not $SCHEME")
        return VirtualFileSystem(this)
    }

    override fun getFileSystem(uri: URI?) = newFileSystem(uri, null)

    override fun getPath(uri: URI): VirtualPath {
        if (uri.scheme?.lowercase() != SCHEME)
            throw IllegalArgumentException("uri scheme is not $SCHEME")
        val user = uri.getUser()
        return VirtualPath(VirtualFileSystem(this), user, uri.path)
    }

    private fun check(path: Path): VirtualPath {
        if (path !is VirtualPath)
            throw ProviderMismatchException("path is not VirtualPath")
        return path
    }

    override fun newByteChannel(
        path: Path,
        options: MutableSet<out OpenOption>?,
        vararg attrs: FileAttribute<*>?
    ): SeekableByteChannel {
        val p = check(path).toFile().toPath()
        return p.fileSystem.provider().newFileChannel(p, options, *attrs)
    }

    override fun newDirectoryStream(dir: Path, filter: DirectoryStream.Filter<in Path>?): DirectoryStream<Path> {
        TODO("Not yet implemented")
    }

    override fun createDirectory(dir: Path?, vararg attrs: FileAttribute<*>?) {
        TODO("Not yet implemented")
    }

    override fun delete(path: Path) {
        TODO("Not yet implemented")
    }

    override fun copy(source: Path, target: Path, vararg options: CopyOption?) {
        TODO("Not yet implemented")
    }

    override fun move(source: Path, target: Path, vararg options: CopyOption?) {
        TODO("Not yet implemented")
    }

    override fun isSameFile(path: Path, path2: Path): Boolean {
        return path.absolute() == path2.absolute()
    }

    override fun isHidden(path: Path): Boolean {
        TODO("Not yet implemented")
    }

    override fun getFileStore(path: Path): FileStore? {
        return getFileSystem(path.toUri()).fileStores.firstOrNull()
    }

    override fun checkAccess(path: Path, vararg modes: AccessMode?) {
        TODO("Not yet implemented")
    }

    override fun <V : FileAttributeView?> getFileAttributeView(
        path: Path,
        type: Class<V>?,
        vararg options: LinkOption?
    ): V {
        TODO("Not yet implemented")
    }

    override fun <A : BasicFileAttributes?> readAttributes(
        path: Path,
        type: Class<A>?,
        vararg options: LinkOption?
    ): A {
        TODO("Not yet implemented")
    }

    override fun readAttributes(
        path: Path,
        attributes: String?,
        vararg options: LinkOption?
    ): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun setAttribute(path: Path, attribute: String?, value: Any?, vararg options: LinkOption?) {
        TODO("Not yet implemented")
    }
}