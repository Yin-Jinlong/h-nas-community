package com.yjl.hnas.service

import com.yjl.hnas.service.virtual.VirtualPath
import com.yjl.hnas.entity.FileMapping
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.VFile
import com.yjl.hnas.entity.VFileId

/**
 * @author YJL
 */
interface VirtualFileService {

    fun getVFileId(uid: Uid?, path: String): String

    fun getVFileId(virtualPath: VirtualPath): String

    fun toVirtualPath(uid: Uid?, path: String): VirtualPath

    fun getVFile(path: VirtualPath): VFile

    fun getFileMapping(fid: VFileId): FileMapping?

    fun getFileMapping(path: VirtualPath): FileMapping


    fun getFiles(path: VirtualPath): List<VFile>

    fun createFolder(dir: VirtualPath, name: String, owner: Uid, public: Boolean)

    val VirtualPath.id: VFileId
        get() = getVFileId(this)
}
