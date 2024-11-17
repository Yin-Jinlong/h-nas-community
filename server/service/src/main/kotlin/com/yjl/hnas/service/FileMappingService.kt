package com.yjl.hnas.service

import com.yjl.hnas.entity.FileMapping
import com.yjl.hnas.fs.PubPath

/**
 * @author YJL
 */
interface FileMappingService {

    fun addMapping(path: PubPath, hash: String)

    fun getMapping(id: String): FileMapping?

    fun deleteMapping(id: String)
}
