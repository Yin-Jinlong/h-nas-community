package com.yjl.hnas.fs

import java.nio.file.*
import java.nio.file.attribute.UserPrincipalLookupService

/**
 * @author YJL
 */
abstract class AbstractFileSystem<FSP :
AbstractFileSystemProvider<*, FSP, FS, P>, FS : AbstractFileSystem<FSP, FS, P>, P : AbstractPath<FSP, FS, P>>(
    protected val fsp: FSP
) : FileSystem() {
    override fun close() = Unit

    override fun provider() = fsp

    override fun isOpen() = true

    override fun isReadOnly() = true

    override fun getSeparator() = "/"

    override fun getRootDirectories(): MutableIterable<P> {
        throw UnsupportedOperationException()
    }

    override fun getFileStores(): MutableIterable<FileStore> {
        throw UnsupportedOperationException()
    }

    override fun supportedFileAttributeViews(): MutableSet<String> {
        throw UnsupportedOperationException()
    }

    fun contactParts(first: String, vararg more: String): String {
        val list = mutableListOf(first, *more)
        for (i in list.indices) {
            list[i] = list[i].replace('\\', '/')
        }
        return list.joinToString("/")
    }

    abstract override fun getPath(first: String, vararg more: String): P

    override fun getPathMatcher(syntaxAndPattern: String?): PathMatcher {
        throw UnsupportedOperationException()
    }

    override fun getUserPrincipalLookupService(): UserPrincipalLookupService {
        throw UnsupportedOperationException()
    }

    override fun newWatchService(): WatchService {
        throw UnsupportedOperationException()
    }

    abstract fun check(path: Path): P
}