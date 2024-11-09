package com.yjl.hnas.fs

import com.yjl.hnas.fs.attr.FileOwnerAttribute
import java.net.URI
import java.nio.file.DirectoryStream
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Path
import java.nio.file.attribute.FileAttribute

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

    override fun createDirectory(dir: Path, vararg attrs: FileAttribute<*>) {
        val p = check(dir)
        if (manager.folderExists(p))
            throw FileAlreadyExistsException("${p.path} already exists")
        val ownerAttr = attrs.find { it is FileOwnerAttribute } as FileOwnerAttribute?
            ?: throw IllegalArgumentException("owner is required")
        manager.createFolder(p, ownerAttr.value())
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