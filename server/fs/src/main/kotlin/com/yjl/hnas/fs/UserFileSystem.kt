package com.yjl.hnas.fs

import java.nio.file.Path

/**
 * @author YJL
 */
class UserFileSystem(
    fsp: UserFileSystemProvider,
    val uid: Long,
) : VirtualableFileSystem<UserFileSystemProvider, UserFileSystem, UserFilePath>(fsp) {
    override fun getPath(first: String, vararg more: String): UserFilePath {
        return UserFilePath(this, contactParts(first, *more)).normalize()
    }

    override fun check(path: Path): UserFilePath {
        if (path !is UserFilePath)
            throw IllegalArgumentException("path is not UserFilePath")
        return path
    }

    override fun toVirtual(path: UserFilePath): VirtualPath {
        TODO()
    }
}