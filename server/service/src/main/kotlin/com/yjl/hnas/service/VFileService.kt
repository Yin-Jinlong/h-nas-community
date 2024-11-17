package com.yjl.hnas.service

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.UserFilePath
import kotlin.io.path.absolutePathString

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

    fun addVFile(owner: Uid, path: PubPath)

    fun addFolder(owner: Uid, path: PubPath)

    fun delete(id: VFileId)
    fun delete(path: PubPath) = delete(genId(path))
    fun delete(path: UserFilePath) = delete(genId(path))
}
