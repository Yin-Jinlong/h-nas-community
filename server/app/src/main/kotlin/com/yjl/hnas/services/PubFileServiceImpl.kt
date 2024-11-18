package com.yjl.hnas.services

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.fs.attr.FileAttribute
import com.yjl.hnas.service.FileMappingService
import com.yjl.hnas.service.PubFileService
import com.yjl.hnas.service.VFileService
import com.yjl.hnas.utils.del
import org.apache.tika.mime.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.AccessMode
import java.nio.file.NoSuchFileException
import kotlin.io.path.absolutePathString
import kotlin.io.path.pathString

/**
 * @author YJL
 */
@Service
class PubFileServiceImpl(
    val vFileService: VFileService,
    val fileMappingService: FileMappingService,
    virtualFileSystemProvider: VirtualFileSystemProvider,
) : PubFileService {

    val virtualFileSystem = virtualFileSystemProvider.getFileSystem()

    override fun checkAccess(path: PubPath, vararg modes: AccessMode) {
        if (!vFileService.exists(path))
            throw NoSuchFileException(path.absolutePathString())
    }

    override fun toVirtualPath(path: PubPath): VirtualPath {
        val vf = vFileService.get(path)
        val mapping = vf?.hash?.let { fileMappingService.getMapping(it) }

        if (mapping == null) {
            val hash = path.bundleAttrs[FileAttribute.HASH]?.value() as String?
                ?: throw IllegalArgumentException("path must have hash attr")
            val type = path.bundleAttrs[FileAttribute.TYPE]?.value() as MediaType?
                ?: throw IllegalArgumentException("path must have type attr")
            if (hash.contains("/"))
                throw IllegalArgumentException("hash must not contain '/'")
            return virtualFileSystem.getPath("data", type.type, type.subtype, hash)
        }
        return virtualFileSystem.getPath("data", mapping.type, mapping.subType, mapping.hash)
    }

    override fun fileExists(path: PubPath): Boolean {
        val id = vFileService.genId(path)
        return vFileService.exists(id)
    }

    @Transactional
    override fun createFolder(path: PubPath, owner: Uid) {
        vFileService.addFolder(owner, path.toAbsolutePath())
    }

    @Transactional
    override fun deleteFile(path: PubPath) {
        if (!fileExists(path))
            throw NoSuchFileException(path.pathString)
        val count = vFileService.getHandlerCount(toVirtualPath(path))
        if (count == 0)
            throw RuntimeException("No hash for path : $path")
        if (count == 1) {
            val f = path.toFile()
            vFileService.delete(path)
            fileMappingService.deleteMapping(vFileService.genId(path))
            f.del()
        } else {
            vFileService.delete(path)
        }
    }
}