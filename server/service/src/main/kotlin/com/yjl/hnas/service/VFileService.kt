package com.yjl.hnas.service

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.IVFile
import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.UserFilePath
import com.yjl.hnas.fs.VirtualPath
import kotlin.io.path.absolutePathString
import kotlin.io.path.name

/**
 * @author YJL
 */
interface VFileService {

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

    fun getById(id: VFileId): IVFile?

    fun get(path: PubPath) = getById(genId(path.toAbsolutePath()))
}
