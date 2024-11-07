package com.yjl.hnas.fs

/**
 * @author YJL
 */
abstract class VirtualablePath<
        FSP : AbstractFileSystemProvider<*, FSP, FS, P>,
        FS : VirtualableFileSystem<FSP, FS, P>,
        P : VirtualablePath<FSP, FS, P>>(
    fs: FS,
    path: String
) : AbstractPath<FSP, FS, P>(fs, path) {

    fun toVirtual() = fs.toVirtual(this as P)
}
