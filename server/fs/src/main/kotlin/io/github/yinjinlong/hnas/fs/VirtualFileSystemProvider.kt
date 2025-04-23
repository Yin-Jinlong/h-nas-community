package io.github.yinjinlong.hnas.fs

import java.net.URI
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import java.nio.file.spi.FileSystemProvider

/**
 * @author YJL
 */
class VirtualFileSystemProvider(
    val manager: VirtualFileManager
) : FileSystemProvider() {

    companion object {
        const val SCHEMA = "virtual"

        fun checkSchema(uri: URI) {
            if (uri.scheme != SCHEMA)
                throw IllegalArgumentException("Scheme must be $SCHEMA , but got '${uri.scheme}'")
        }
    }

    val virtualFilesystem = VirtualFilesystem(this)

    init {
        manager.onBind(this)
    }

    override fun getScheme() = SCHEMA

    override fun newFileSystem(uri: URI, env: MutableMap<String, *>): VirtualFilesystem {
        checkSchema(uri)
        return virtualFilesystem
    }

    override fun getFileSystem(uri: URI) = newFileSystem(uri, mutableMapOf<String, Any>())

    override fun getPath(uri: URI): VirtualPath {
        checkSchema(uri)
        val access = uri.host.toLongOrNull()?.toString() ?: ""
        return virtualFilesystem.getPath(access, arrayOf(uri.path))
    }

    private fun Array<out FileAttribute<*>>.toMap(): Map<String, FileAttribute<*>> = buildMap {
        for (attr in this@toMap) {
            this[attr.name()] = attr
        }
    }

    override fun newByteChannel(
        path: Path,
        options: MutableSet<out OpenOption>,
        vararg attrs: FileAttribute<*>
    ) = manager.newByteChannel(VirtualPath.check(path), options, attrs.toMap())

    override fun newDirectoryStream(dir: Path, filter: DirectoryStream.Filter<in Path>): DirectoryStream<Path> =
        manager.newDirectoryStream(VirtualPath.check(dir), filter) as DirectoryStream<Path>

    override fun createDirectory(dir: Path, vararg attrs: FileAttribute<*>) =
        manager.createDirectory(VirtualPath.check(dir), attrs.toMap())

    override fun delete(path: Path) = manager.delete(VirtualPath.check(path))

    override fun copy(source: Path, target: Path, vararg options: CopyOption) =
        manager.copy(VirtualPath.check(source), VirtualPath.check(target), options.toSet())

    override fun move(source: Path, target: Path, vararg options: CopyOption) =
        manager.move(VirtualPath.check(source), VirtualPath.check(target), options.toSet())

    override fun isSameFile(path: Path, path2: Path) =
        manager.isSameFile(VirtualPath.check(path), VirtualPath.check(path2))

    override fun isHidden(path: Path) =
        manager.isHidden(VirtualPath.check(path))

    override fun getFileStore(path: Path): FileStore =
        manager.getFileStore(VirtualPath.check(path))

    override fun checkAccess(path: Path, vararg modes: AccessMode) =
        manager.checkAccess(VirtualPath.check(path), modes.toSet())

    override fun <V : FileAttributeView> getFileAttributeView(
        path: Path,
        type: Class<V>,
        vararg options: LinkOption
    ): V? = manager.getFileAttributeView(VirtualPath.check(path), type, options.toSet())

    override fun <A : BasicFileAttributes> readAttributes(
        path: Path,
        type: Class<A>,
        vararg options: LinkOption
    ): A = manager.readAttributes(VirtualPath.check(path), type, options.toSet())

    override fun readAttributes(
        path: Path,
        attributes: String,
        vararg options: LinkOption
    ): MutableMap<String, Any> =
        manager.readAttributes(VirtualPath.check(path), attributes, options.toSet())

    override fun setAttribute(path: Path, attribute: String, value: Any?, vararg options: LinkOption) =
        manager.setAttribute(VirtualPath.check(path), attribute, value, options.toSet())
}
