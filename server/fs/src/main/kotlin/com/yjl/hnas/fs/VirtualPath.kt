package com.yjl.hnas.fs

import java.io.File

/**
 * @author YJL
 */
class VirtualPath(
    fs: VirtualFileSystem,
    path: String,
) : AbstractPath<VirtualFileSystemProvider, VirtualFileSystem, VirtualPath>(fs, path) {
    override fun clone(path: String, absolute: Boolean): VirtualPath {
        return VirtualPath(fs, path)
    }

    override fun toFile(): File {
        return fs.toFile(this)
    }
}
