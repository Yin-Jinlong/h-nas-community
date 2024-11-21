package com.yjl.hnas.service

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.IVirtualFile
import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.UserFilePath
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.fs.VirtualPathManager
import kotlin.io.path.absolutePathString
import kotlin.io.path.name

/**
 * @author YJL
 */
interface VirtualFileService : VirtualPathManager {

    fun genId(access: String, path: String): VFileId
    fun genId(path: PubPath) = genId("", path.absolutePathString())
    fun genId(path: UserFilePath): VFileId = genId("${path.uid}", path.absolutePathString())

    fun exists(id: VFileId): Boolean
    fun exists(path: PubPath): Boolean = exists(genId(path.toAbsolutePath()))
    fun exists(path: UserFilePath): Boolean = exists(genId(path.toAbsolutePath()))

    fun addVFile(owner: Uid, path: PubPath, size: Long, hash: String)

    fun addFolder(owner: Uid, path: PubPath)

    fun delete(path: PubPath)


    /**
     *
     * @return 0没有，1有一个，2有更多
     */
    fun getHandlerCount(hash: String): Int

    fun getHandlerCount(path: VirtualPath): Int = getHandlerCount(path.name)

    fun getById(id: VFileId): IVirtualFile?
    fun getByParent(path: PubPath): List<IVirtualFile> = getByParent(genId(path.toAbsolutePath()))
    fun getByParent(parent: VFileId): List<IVirtualFile>

    fun get(path: PubPath) = getById(genId(path.toAbsolutePath()))

    fun createPubFile(owner: Uid, path: PubPath, size: Long, hash: String)
}
