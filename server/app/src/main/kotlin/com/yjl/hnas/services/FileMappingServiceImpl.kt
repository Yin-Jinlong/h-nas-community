package com.yjl.hnas.services

import com.yjl.hnas.entity.FileMapping
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.mapper.FileMappingMapper
import com.yjl.hnas.service.FileMappingService
import com.yjl.hnas.service.VFileService
import com.yjl.hnas.tika.FileDetector
import com.yjl.hnas.utils.base64Url
import io.github.yinjinlong.md.sha256
import org.apache.tika.mime.MediaType
import org.springframework.stereotype.Service
import kotlin.io.path.name

/**
 * @author YJL
 */
@Service
class FileMappingServiceImpl(
    val vFileService: VFileService,
    val fileMappingMapper: FileMappingMapper
) : FileMappingService {

    override fun addMapping(path: PubPath, hash: String) {
        val vp = path.toVirtual()
        val file = vp.toFile()
        val fileHash = file.inputStream().use {
            it.sha256
        }.base64Url
        if (fileHash != hash)
            throw ErrorCode.BAD_HEADER.data("bad hash")
        val type: MediaType
        file.inputStream().buffered().use {
            type = FileDetector.detect(it, null)
        }
        fileMappingMapper.insert(
            FileMapping(
                fid = vFileService.genId(path),
                dataPath = vp.path,
                hash = fileHash,
                type = type.type,
                subType = type.subtype
            )
        )
    }

    override fun getHandlerCount(hash: String): Int {
        return fileMappingMapper.countHash(hash)
    }

    override fun getHandlerCount(path: VirtualPath): Int {
        return getHandlerCount(path.name)
    }

    override fun getMapping(id: String): FileMapping? {
        return fileMappingMapper.selectById(id)
    }

    override fun deleteMapping(id: String) {
        fileMappingMapper.deleteById(id)
    }
}
