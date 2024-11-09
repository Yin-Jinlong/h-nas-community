package com.yjl.hnas.fs

import com.yjl.hnas.entity.Uid
import java.net.URI

/**
 * @author YJL
 */
class UserFilePath(
    fs: UserFileSystem,
    path: String
) : VirtualablePath<UserFileSystemProvider, UserFileSystem, UserFilePath>(fs, path) {

    val uid: Uid
        get() = fs.uid

    override fun clone(path: String): UserFilePath {
        return UserFilePath(fs, path)
    }

    override fun toUri(): URI {
        return URI(
            UserFileSystemProvider.SCHEME,
            fs.uid.toString(),
            "null",
            0,
            path,
            null,
            null
        )
    }
}
