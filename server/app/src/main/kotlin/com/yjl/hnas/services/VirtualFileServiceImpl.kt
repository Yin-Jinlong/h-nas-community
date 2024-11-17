package com.yjl.hnas.services

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.entity.view.VirtualFile
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.UserFilePath
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.mapper.VirtualFileMapper
import com.yjl.hnas.service.FileMappingService
import com.yjl.hnas.service.VFileService
import com.yjl.hnas.service.VirtualFileService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.nio.file.AccessMode

/**
 * @author YJL
 */
@Service
class VirtualFileServiceImpl(
    val vFileService: VFileService,
    val fileMappingService: FileMappingService,
    val virtualFileMapper: VirtualFileMapper,
) : VirtualFileService {
    override fun checkAccess(path: VirtualPath, vararg modes: AccessMode) {

    }

    override fun getFilesByParent(parent: VFileId): List<VirtualFile> {
        return virtualFileMapper.selectsByParent(parent)
    }

    override fun getFilesByParent(parent: PubPath): List<VirtualFile> {
        return getFilesByParent(vFileService.genId(parent))
    }

    override fun getFilesByParent(parent: UserFilePath): List<VirtualFile> {
        return getFilesByParent(vFileService.genId(parent))
    }

    override fun convertToFile(path: VirtualPath): File {
        return File(path.path)
    }

    @Transactional
    override fun createPubFile(
        user: Uid,
        path: PubPath,
        hash: String
    ) {
        if (vFileService.exists(path))
            throw ErrorCode.FILE_EXISTS.data(path)
        if (!vFileService.exists(path.parent))
            vFileService.addFolder(user, path.parent)
        vFileService.addVFile(user, path, hash)
        if (vFileService.getHandlerCount(hash) == 1)
            fileMappingService.addMapping(path, hash)
    }
}