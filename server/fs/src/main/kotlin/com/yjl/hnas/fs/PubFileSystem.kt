package com.yjl.hnas.fs

import java.nio.file.Path

/**
 * @author YJL
 */
class PubFileSystem(
    fsp: PubFileSystemProvider
) : VirtualableFileSystem<PubFileSystemProvider, PubFileSystem, PubPath>(fsp) {
    override fun getPath(first: String, vararg more: String): PubPath {
        return PubPath(this, if (more.isEmpty()) "" else more.joinToString("/", "/"))
    }

    override fun check(path: Path): PubPath {
        if (path !is PubPath)
            throw IllegalArgumentException("path is not PubPath")
        return path
    }

    override fun toVirtual(path: PubPath): VirtualPath {
        return provider().manager.toVirtualPath(path)
    }
}