package io.github.yinjinlong.hnas.fs.test

import io.github.yinjinlong.hnas.fs.VirtualFileManager
import io.github.yinjinlong.hnas.fs.VirtualFileSystemProvider
import io.github.yinjinlong.hnas.fs.VirtualPath
import java.nio.channels.SeekableByteChannel
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView

/**
 * @author YJL
 */
class VirtualFileManagerImpl : VirtualFileManager {
    override fun onBind(fsp: VirtualFileSystemProvider) {

    }

    override fun newByteChannel(
        path: VirtualPath,
        options: MutableSet<out OpenOption>,
        attrs: Map<String, FileAttribute<*>>
    ): SeekableByteChannel {
        TODO("Not yet implemented")
    }

    override fun newDirectoryStream(
        dir: VirtualPath,
        filter: DirectoryStream.Filter<in VirtualPath>
    ): DirectoryStream<VirtualPath> {
        TODO("Not yet implemented")
    }

    override fun createDirectory(dir: VirtualPath, attrs: Map<String, FileAttribute<*>>) {
        TODO("Not yet implemented")
    }

    override fun delete(path: VirtualPath) {
        TODO("Not yet implemented")
    }

    override fun copy(source: VirtualPath, target: VirtualPath, options: Set<CopyOption>) {
        TODO("Not yet implemented")
    }

    override fun move(source: VirtualPath, target: VirtualPath, options: Set<CopyOption>) {
        TODO("Not yet implemented")
    }

    override fun isSameFile(path: VirtualPath, path2: VirtualPath): Boolean {
        TODO("Not yet implemented")
    }

    override fun isHidden(path: VirtualPath): Boolean {
        TODO("Not yet implemented")
    }

    override fun getFileStore(path: VirtualPath): FileStore {
        TODO("Not yet implemented")
    }

    override fun checkAccess(path: VirtualPath, modes: Set<AccessMode>) {
        TODO("Not yet implemented")
    }

    override fun <V : FileAttributeView> getFileAttributeView(
        path: VirtualPath,
        type: Class<V>,
        options: Set<LinkOption>
    ): V? {
        TODO("Not yet implemented")
    }

    override fun <A : BasicFileAttributes> readAttributes(
        path: VirtualPath,
        type: Class<A>,
        options: Set<LinkOption>
    ): A {
        TODO("Not yet implemented")
    }

    override fun readAttributes(
        path: VirtualPath,
        attributes: String,
        options: Set<LinkOption>
    ): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun setAttribute(path: VirtualPath, attribute: String, value: Any?, options: Set<LinkOption>) {
        TODO("Not yet implemented")
    }
}