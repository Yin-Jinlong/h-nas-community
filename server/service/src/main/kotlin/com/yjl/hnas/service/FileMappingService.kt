package com.yjl.hnas.service

import com.yjl.hnas.entity.FileMapping
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.VirtualPath

/**
 * @author YJL
 */
interface FileMappingService {

    fun addMapping(path: PubPath, hash: String)

    /**
     *
     * @return 1有一个，2有更多
     */
    fun getHandlerCount(hash: String): Int

    fun getHandlerCount(path: VirtualPath): Int

    fun getMapping(id: String): FileMapping?

    fun deleteMapping(id: String)
}
