package com.yjl.hnas.fs

import java.net.URI
import java.nio.file.Path
import java.nio.file.attribute.FileAttribute

/**
 * @author YJL
 */
class UserFileSystemProvider(
    manager: UserPathManager
) : AbstractFileSystemProvider<
        UserPathManager,
        UserFileSystemProvider,
        UserFileSystem,
        UserFilePath
        >(manager) {

    companion object {
        const val SCHEME = "userfile"
    }

    override fun getScheme() = SCHEME

    override fun newFileSystem(uri: URI, env: MutableMap<String, *>): UserFileSystem {
        checkScheme(uri)
        val userId = uri.userInfo ?: ""
        if (userId.isBlank())
            throw IllegalArgumentException("no userId (userInfo)")
        return UserFileSystem(
            this, userId.toLongOrNull()
                ?: throw IllegalArgumentException("userId must be long")
        )
    }

    fun getFileSystem(uid: Long): UserFileSystem {
        return UserFileSystem(this, uid)
    }

    override fun createDirectory(dir: Path, vararg attrs: FileAttribute<*>) {
        val p = check(dir)
        manager.createDirectory(p)
    }

    override fun isSameFile(path1: UserFilePath, path2: UserFilePath): Boolean {
        TODO("Not yet implemented")
    }

}