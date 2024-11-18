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
import org.springframework.transaction.annotation.Transactional
import java.io.File

/**
 * @author YJL
 */
@Service
class FileMappingServiceImpl(
    val fileMappingMapper: FileMappingMapper
) : FileMappingService {

    @Transactional
    override fun addMapping(path: PubPath, size: Long, hash: String) {
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
                subType = type.subtype,
                size = file.length().also {
                    if (it != size)
                        throw IllegalStateException("size not match : $it != $size")
                }
            )
        )
    }

    override fun getMapping(hash: String): FileMapping? {
        return fileMappingMapper.selectByHash(hash)
    }

    @Transactional
    override fun deleteMapping(hash: String) {
        fileMappingMapper.deleteById(hash)
    }

    override fun getSize(hash: String): Long {
        val fm = getMapping(hash) ?: throw IllegalArgumentException("hash not found")
        if (fm.size < 0) {
            fm.size = File(fm.dataPath).length()
            fileMappingMapper.updateSize(hash, fm.size)
        }
        return fm.size
    }
}
