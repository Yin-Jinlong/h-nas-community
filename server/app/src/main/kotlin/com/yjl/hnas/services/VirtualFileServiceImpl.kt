package com.yjl.hnas.services

import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.entity.view.VirtualFile
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.mapper.VirtualFileMapper
import com.yjl.hnas.service.VirtualFileService
import io.github.yinjinlong.md.sha256
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.AccessMode
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * @author YJL
 */
@Service
class VirtualFileServiceImpl(
    val virtualFileMapper: VirtualFileMapper
) : VirtualFileService {
    override fun checkAccess(path: VirtualPath, vararg modes: AccessMode) {

    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun genId(access: String, path: String): VFileId {
        return Base64.encode("$access:$path".sha256)
    }

    override fun getFilesByParent(parent: VFileId): List<VirtualFile> {
        return virtualFileMapper.selectsByParent(parent)
    }

    override fun convertToFile(path: VirtualPath): File {
        TODO("Not yet implemented")
    }
}