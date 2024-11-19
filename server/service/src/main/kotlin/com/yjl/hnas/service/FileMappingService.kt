package com.yjl.hnas.service

import com.yjl.hnas.entity.IFileMapping
import com.yjl.hnas.fs.PubPath

/**
 * @author YJL
 */
interface FileMappingService {

    fun addMapping(path: PubPath, size: Long, hash: String)

    fun getMapping(hash: String): IFileMapping?

    fun deleteMapping(hash: String)

    fun getSize(hash: String): Long
}
