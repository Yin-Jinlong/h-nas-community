package com.yjl.hnas.services

import com.yjl.hnas.entity.FileMapping
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.mapper.FileMappingMapper
import com.yjl.hnas.service.FileMappingService
import com.yjl.hnas.tika.FileDetector
import com.yjl.hnas.utils.base64Url
import io.github.yinjinlong.md.sha256
import org.apache.tika.mime.MediaType
import org.springframework.stereotype.Service

/**
 * @author YJL
 */
@Service
class FileMappingServiceImpl(
    val fileMappingMapper: FileMappingMapper
) : FileMappingService {

    override fun addMapping(path: PubPath, hash: String) {
        val vp = path.toVirtual()
        val file = vp.toFile()
        val fileHash = file.sha256.base64Url
        if (fileHash != hash)
            throw ErrorCode.BAD_HEADER.data("bad hash")
        val type: MediaType
        file.inputStream().buffered().use {
            type = FileDetector.detect(it, null)
        }
        fileMappingMapper.insert(
            FileMapping(
                hash = fileHash,
                dataPath = vp.path,
                type = type.type,
                subType = type.subtype
            )
        )
    }

    override fun getMapping(id: String): FileMapping? {
        return fileMappingMapper.selectByHash(id)
    }

    override fun deleteMapping(id: String) {
        fileMappingMapper.deleteById(id)
    }
}
