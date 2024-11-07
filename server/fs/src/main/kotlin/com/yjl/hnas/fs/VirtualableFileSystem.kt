package com.yjl.hnas.fs

/**
 * @author YJL
 */
abstract class VirtualableFileSystem<
        FSP : AbstractFileSystemProvider<*, FSP, FS, P>,
        FS : VirtualableFileSystem<FSP, FS, P>,
        P : VirtualablePath<FSP, FS, P>>(
    provider: FSP
) : AbstractFileSystem<FSP, FS, P>(provider) {
    abstract fun toVirtual(path: P): VirtualPath
}