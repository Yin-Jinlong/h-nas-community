package com.yjl.hnas.service

import com.yjl.hnas.data.FileRange
import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.entity.Hash
import com.yjl.hnas.entity.IVirtualFile
import com.yjl.hnas.fs.VirtualFileManager
import com.yjl.hnas.fs.VirtualPath
import java.io.BufferedInputStream
import java.io.IOException
import java.lang.IllegalArgumentException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.NoSuchFileException

/**
 * @author YJL
 */
interface VirtualFileService : VirtualFileManager {

    fun exists(path: VirtualPath): Boolean

    fun genId(path: VirtualPath): Hash

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
        hash: Hash,
        fileSize: Long,
        range: FileRange,
        ins: BufferedInputStream
    ): Boolean

    @Throws(
        NoSuchFileException::class,
        IOException::class
    )
    fun rename(path: VirtualPath, name: String)
}
