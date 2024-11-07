package com.yjl.hnas.service

import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.entity.view.VirtualFile
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.UserFilePath
import com.yjl.hnas.fs.VirtualPathManager

/**
 * @author YJL
 */
interface VirtualFileService : VirtualPathManager {

    fun genId(access: String, path: String): VFileId

    val PubPath.id: VFileId
        get() = genId("", path)

    val UserFilePath.id: VFileId
        get() = genId("$uid", path)

    fun getFilesByParent(parent: VFileId): List<VirtualFile>
    fun getFilesByParent(parent: PubPath): List<VirtualFile> = getFilesByParent(parent.toAbsolutePath().id)
    fun getFilesByParent(parent: UserFilePath): List<VirtualFile> = getFilesByParent(parent.toAbsolutePath().id)
}
