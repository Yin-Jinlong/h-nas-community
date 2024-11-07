package com.yjl.hnas.fs

/**
 * @author YJL
 */
class PubPath(
    fileSystem: PubFileSystem,
    path: String
) : VirtualablePath<PubFileSystemProvider, PubFileSystem, PubPath>(fileSystem, path) {
    override fun clone(path: String, absolute: Boolean): PubPath {
        return PubPath(fs, path)
    }
}