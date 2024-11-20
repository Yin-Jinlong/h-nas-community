package com.yjl.hnas.service

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.entity.view.IVirtualFile
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.UserFilePath
import com.yjl.hnas.fs.VirtualPathManager

/**
 * @author YJL
 */
interface VirtualFileService : VirtualPathManager {

    fun getFile(path: PubPath): IVirtualFile?

    fun getFilesByParent(parent: VFileId): List<IVirtualFile>
    fun getFilesByParent(parent: PubPath): List<IVirtualFile>
    fun getFilesByParent(parent: UserFilePath): List<IVirtualFile>

    fun createPubFile(user: Uid, path: PubPath, size: Long, hash: String)
}
