package com.yjl.hnas.service

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.entity.view.IVirtualFile
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.UserFilePath
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.mapper.VirtualFileMapper
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

    override fun getFile(path: PubPath): IVirtualFile? {
        return virtualFileMapper.selectById(vFileService.genId(path))
    }

    override fun getFilesByParent(parent: VFileId): List<IVirtualFile> {
        if (!vFileService.exists(parent))
            throw ErrorCode.NO_SUCH_FILE.error
        return virtualFileMapper.selectsByParent(parent)
    }

    override fun getFilesByParent(parent: PubPath): List<IVirtualFile> {
        return getFilesByParent(vFileService.genId(parent))
    }

    override fun getFilesByParent(parent: UserFilePath): List<IVirtualFile> {
        return getFilesByParent(vFileService.genId(parent))
    }

    override fun convertToFile(path: VirtualPath): File {
        return File(path.path)
    }

    @Transactional
    override fun createPubFile(
        user: Uid,
        path: PubPath,
        size: Long,
        hash: String
    ) {
        if (vFileService.exists(path))
            throw ErrorCode.FILE_EXISTS.data(path)
        if (!vFileService.exists(path.parent))
            vFileService.addFolder(user, path.parent)
        vFileService.addVFile(user, path, size, hash)
        if (fileMappingService.getMapping(hash) == null)
            fileMappingService.addMapping(path, size, hash)
    }
}