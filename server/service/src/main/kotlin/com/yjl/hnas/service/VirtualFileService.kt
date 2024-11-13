package com.yjl.hnas.service

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.entity.view.VirtualFile
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.UserFilePath
import com.yjl.hnas.fs.VirtualPathManager
import kotlin.io.path.pathString

/**
 * @author YJL
 */
interface VirtualFileService : VirtualPathManager {

    fun genId(access: String, path: String): VFileId
    fun genId(path: PubPath) = path.id
    fun genId(path: UserFilePath): VFileId = path.id
    fun genPubId(path: String): VFileId = genId("", path)

    val PubPath.id: VFileId
        get() = genId("", pathString)

    val UserFilePath.id: VFileId
        get() = genId("$uid", pathString)

    fun getFilesByParent(parent: VFileId): List<VirtualFile>
    fun getFilesByParent(parent: PubPath): List<VirtualFile> = getFilesByParent(parent.toAbsolutePath().id)
    fun getFilesByParent(parent: UserFilePath): List<VirtualFile> = getFilesByParent(parent.toAbsolutePath().id)

    fun createPubFile(user: Uid, path: PubPath, hash: String, type: String, subType: String)
}
