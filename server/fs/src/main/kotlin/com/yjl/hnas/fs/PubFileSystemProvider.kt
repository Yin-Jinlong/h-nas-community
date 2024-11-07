package com.yjl.hnas.fs

import java.net.URI
import java.nio.file.DirectoryStream
import java.nio.file.Path

/**
 * @author YJL
 */
class PubFileSystemProvider(
    manager: PubPathManager
) : AbstractFileSystemProvider<
        PubPathManager,
        PubFileSystemProvider,
        PubFileSystem,
        PubPath>(
    manager
) {

    companion object {
        const val SCHEME = "pubfile"
    }

    override fun newFileSystem(uri: URI, env: MutableMap<String, *>): PubFileSystem {
        checkScheme(uri)
        return PubFileSystem(this)
    }

    override fun newDirectoryStream(dir: Path, filter: DirectoryStream.Filter<in Path>): DirectoryStream<Path> {
        throw UnsupportedOperationException()
    }

    override fun isSameFile(path1: PubPath, path2: PubPath): Boolean {
        TODO("Not yet implemented")
    }

    override fun getScheme() = SCHEME

    fun getFileSystem() = PubFileSystem(this)
}