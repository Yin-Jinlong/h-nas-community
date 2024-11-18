package com.yjl.hnas.service

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.entity.view.VirtualFile
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.UserFilePath
import com.yjl.hnas.fs.VirtualPathManager

/**
 * @author YJL
 */
interface VirtualFileService : VirtualPathManager {

    fun getFilesByParent(parent: VFileId): List<VirtualFile>
    fun getFilesByParent(parent: PubPath): List<VirtualFile>
    fun getFilesByParent(parent: UserFilePath): List<VirtualFile>

    fun createPubFile(user: Uid, path: PubPath, size: Long, hash: String)
}
