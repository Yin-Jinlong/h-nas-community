package com.yjl.hnas.fs

import java.io.File
import java.nio.file.Path

/**
 * @author YJL
 */
class VirtualFileSystem(
    provider: VirtualFileSystemProvider
) : AbstractFileSystem<VirtualFileSystemProvider, VirtualFileSystem, VirtualPath>(provider) {
    override fun getPath(first: String, vararg more: String): VirtualPath {
        return VirtualPath(this, contactParts(first, *more)).normalize()
    }

    override fun check(path: Path): VirtualPath {
        if (path !is VirtualPath)
            throw IllegalArgumentException("path is not VirtualPath")
        return path
    }

    fun toFile(path: VirtualPath): File {
        return provider().toFile(path)
    }
}
