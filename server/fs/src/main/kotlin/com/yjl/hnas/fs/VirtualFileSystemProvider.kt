package com.yjl.hnas.fs

import java.io.File
import java.net.URI
import java.nio.file.FileSystems

/**
 *
 * @author YJL
 */
class VirtualFileSystemProvider(
    manager: VirtualPathManager,
) : AbstractFileSystemProvider<
        VirtualPathManager,
        VirtualFileSystemProvider,
        VirtualFileSystem,
        VirtualPath
        >(manager) {
    companion object {
        const val SCHEME = "virtual"
    }

    override fun getScheme() = SCHEME

    override fun newFileSystem(uri: URI, env: MutableMap<String, *>): VirtualFileSystem {
        checkScheme(uri)
        return VirtualFileSystem(this)
    }

    override fun isSameFile(path1: VirtualPath, path2: VirtualPath): Boolean {
        val p1 = path1.toFile().toPath()
        val p2 = path2.toFile().toPath()
        return FileSystems.getDefault().provider().isSameFile(p1, p2)
    }

    fun toFile(path: VirtualPath): File {
        return manager.convertToFile(path)
    }

}