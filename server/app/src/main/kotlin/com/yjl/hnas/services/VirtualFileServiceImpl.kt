package com.yjl.hnas.services

import com.yjl.hnas.entity.FileMapping
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.VFile
import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.entity.view.VirtualFile
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.mapper.FileMappingMapper
import com.yjl.hnas.mapper.VFileMapper
import com.yjl.hnas.mapper.VirtualFileMapper
import com.yjl.hnas.service.VirtualFileService
import com.yjl.hnas.utils.base64
import com.yjl.hnas.utils.base64Url
import com.yjl.hnas.utils.timestamp
import io.github.yinjinlong.md.sha256
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.nio.file.AccessMode
import kotlin.io.path.name

/**
 * @author YJL
 */
@Service
class VirtualFileServiceImpl(
    val fileMappingMapper: FileMappingMapper,
    val vFileMapper: VFileMapper,
    val virtualFileMapper: VirtualFileMapper,
) : VirtualFileService {
    override fun checkAccess(path: VirtualPath, vararg modes: AccessMode) {

    }

    override fun genId(access: String, path: String): VFileId {
        return "$access:$path".sha256.base64
    }

    override fun getFilesByParent(parent: VFileId): List<VirtualFile> {
        return virtualFileMapper.selectsByParent(parent)
    }

    override fun convertToFile(path: VirtualPath): File {
        return File(path.path)
    }

    @Transactional
    override fun createPubFile(
        user: Uid,
        path: PubPath,
        hash: String,
        type: String,
        subType: String
    ) {
        val time = System.currentTimeMillis().timestamp
        val id = genId(user.toString(), path.path)
        vFileMapper.insert(
            VFile(
                fid = id,
                name = path.name,
                parent = genId("", path.parent.toAbsolutePath().fullPath),
                owner = user,
                createTime = time,
                updateTime = time,
                type = VFile.Type.FILE
            )
        )
        fileMappingMapper.insert(
            FileMapping(
                fid = id,
                dataPath = path.fullPath,
                hash = path.toVirtual().toFile().sha256.base64Url.also {
                    if (hash.trimEnd('=') != it.trimEnd('='))
                        throw IllegalArgumentException("hash not match")
                },
                type = type,
                subType = subType
            )
        )
    }
}