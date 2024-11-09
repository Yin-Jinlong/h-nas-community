package com.yjl.hnas.fs

import com.yjl.hnas.entity.Uid
import kotlin.jvm.Throws

/**
 * @author YJL
 */
interface PubPathManager : PathManager<PubPath> {

    fun toVirtualPath(path: PubPath): VirtualPath

    fun folderExists(path: PubPath): Boolean

    /**
     * 创建文件夹，不会做判断
     */
    @Throws
    fun createFolder(path: PubPath, owner: Uid)
}
