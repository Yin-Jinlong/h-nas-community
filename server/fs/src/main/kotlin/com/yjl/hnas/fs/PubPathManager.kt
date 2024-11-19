package com.yjl.hnas.fs

/**
 * @author YJL
 */
interface PubPathManager : PathManager<PubPath> {

    fun toVirtualPath(path: PubPath): VirtualPath

    fun fileExists(path: PubPath): Boolean

    /**
     * 创建文件夹，不会做判断
     */
    @Throws
    fun createFolder(path: PubPath, owner: Long)

    @Throws(NoSuchFileException::class)
    fun deleteFile(path: PubPath)
}
