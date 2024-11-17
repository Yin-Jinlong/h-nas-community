package com.yjl.hnas.fs

import com.yjl.hnas.entity.Uid
import java.io.FileNotFoundException
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
        if (manager.fileExists(p))
            throw FileAlreadyExistsException("${p.path} already exists")
        val ownerAttr = getAttribute(attrs, com.yjl.hnas.fs.attr.FileAttribute.OWNER)
            ?: throw IllegalArgumentException("owner is required")
        manager.createFolder(p, ownerAttr.value() as Uid)
    }

    override fun delete(path: Path) {
        val p = check(path)
        manager.deleteFile(p.toAbsolutePath())
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