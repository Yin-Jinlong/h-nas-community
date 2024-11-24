package com.yjl.hnas.fs

import java.io.IOException
import java.lang.ClassCastException
import java.lang.IllegalArgumentException
import java.nio.channels.SeekableByteChannel
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView

/**
 * @author YJL
 */
interface VirtualFileManager {

    fun onBind(fsp: VirtualFileSystemProvider)

    @Throws(
        IllegalArgumentException::class,
        UnsupportedOperationException::class,
        FileAlreadyExistsException::class,
        IOException::class,
        SecurityException::class
    )
    fun newByteChannel(
        path: VirtualPath,
        options: MutableSet<out OpenOption>,
        attrs: Map<String, FileAttribute<*>>
    ): SeekableByteChannel

    @Throws(
        NotDirectoryException::class,
        IOException::class,
        SecurityException::class
    )
    fun newDirectoryStream(
        dir: VirtualPath,
        filter: DirectoryStream.Filter<in VirtualPath>
    ): DirectoryStream<VirtualPath>

    @Throws(
        UnsupportedOperationException::class,
        FileAlreadyExistsException::class,
        IllegalArgumentException::class,
        IOException::class,
        SecurityException::class
    )
    fun createDirectory(dir: VirtualPath, attrs: Map<String, FileAttribute<*>>)

    @Throws(
        NoSuchFileException::class,
        DirectoryNotEmptyException::class,
        IOException::class,
        SecurityException::class
    )
    fun delete(path: VirtualPath)

    @Throws(
        UnsupportedOperationException::class,
        FileAlreadyExistsException::class,
        DirectoryNotEmptyException::class,
        AtomicMoveNotSupportedException::class,
        IOException::class,
        SecurityException::class
    )
    fun copy(source: VirtualPath, target: VirtualPath, options: Set<CopyOption>)

    @Throws(
        UnsupportedOperationException::class,
        FileAlreadyExistsException::class,
        DirectoryNotEmptyException::class,
        AtomicMoveNotSupportedException::class,
        IOException::class,
        SecurityException::class
    )
    fun move(source: VirtualPath, target: VirtualPath, options: Set<CopyOption>)

    @Throws(
        IOException::class,
        SecurityException::class
    )
    fun isSameFile(path: VirtualPath, path2: VirtualPath): Boolean

    @Throws(
        IOException::class,
        SecurityException::class
    )
    fun isHidden(path: VirtualPath): Boolean

    @Throws(
        IOException::class,
        SecurityException::class
    )
    fun getFileStore(path: VirtualPath): FileStore

    @Throws(
        UnsupportedOperationException::class,
        NoSuchFileException::class,
        AccessDeniedException::class,
        IOException::class,
        SecurityException::class
    )
    fun checkAccess(path: VirtualPath, modes: Set<AccessMode>)

    fun <V : FileAttributeView> getFileAttributeView(
        path: VirtualPath,
        type: Class<V>,
        options: Set<LinkOption>
    ): V?

    @Throws(
        UnsupportedOperationException::class,
        IOException::class,
        SecurityException::class
    )
    fun <A : BasicFileAttributes> readAttributes(
        path: VirtualPath,
        type: Class<A>,
        options: Set<LinkOption>
    ): A

    @Throws(
        UnsupportedOperationException::class,
        IllegalArgumentException::class,
        IOException::class,
        SecurityException::class
    )
    fun readAttributes(
        path: VirtualPath,
        attributes: String,
        options: Set<LinkOption>
    ): MutableMap<String, Any>

    @Throws(
        UnsupportedOperationException::class,
        IllegalArgumentException::class,
        ClassCastException::class,
        IOException::class,
        SecurityException::class
    )
    fun setAttribute(path: VirtualPath, attribute: String, value: Any?, options: Set<LinkOption>)
}
