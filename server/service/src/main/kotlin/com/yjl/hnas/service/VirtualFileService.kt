package com.yjl.hnas.service

import com.yjl.hnas.entity.IVirtualFile
import com.yjl.hnas.fs.VirtualFileManager
import com.yjl.hnas.fs.VirtualPath
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
}
