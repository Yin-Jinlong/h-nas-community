package com.yjl.hnas.service

import com.yjl.hnas.data.FileRange
import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.entity.IVirtualFile
import com.yjl.hnas.fs.VirtualFileManager
import com.yjl.hnas.fs.VirtualPath
import java.io.IOException
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.NoSuchFileException

/**
 * @author YJL
 */
interface VirtualFileService : VirtualFileManager {

    fun exists(path: VirtualPath): Boolean

    fun genId(path: VirtualPath): String

    fun get(path: VirtualPath): IVirtualFile?

    @Throws(NoSuchFileException::class)
    fun getByParent(parent: VirtualPath): List<IVirtualFile>

    @Throws(
        FileAlreadyExistsException::class,
        IllegalArgumentException::class,
        NoSuchFileException::class,
        IOException::class
    )
    fun upload(
        user: UserInfo,
        path: VirtualPath,
        hash: String,
        fileSize: Long,
        range: FileRange,
        ins: InputStream
    ): Boolean
}
