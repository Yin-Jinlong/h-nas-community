package com.yjl.hnas.service

import com.yjl.hnas.entity.FileMapping
import com.yjl.hnas.fs.PubPath

/**
 * @author YJL
 */
interface FileMappingService {

    fun addMapping(path: PubPath, hash: String)

    fun getMapping(hash: String): FileMapping?

    fun deleteMapping(hash: String)
}
