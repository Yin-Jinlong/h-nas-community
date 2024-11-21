package com.yjl.hnas.service

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.fs.attr.FileAttribute
import com.yjl.hnas.utils.FileTypeAttribute
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
    val virtualFileService: VirtualFileService,
    val fileMappingService: FileMappingService,
    virtualFileSystemProvider: VirtualFileSystemProvider,
) : PubFileService {

    val virtualFileSystem = virtualFileSystemProvider.getFileSystem()

    override fun checkAccess(path: PubPath, vararg modes: AccessMode) {
        if (!virtualFileService.exists(path))
            throw NoSuchFileException(path.absolutePathString())
    }

    override fun toVirtualPath(path: PubPath): VirtualPath {
        val vf = virtualFileService.get(path)
        val mapping = vf?.hash?.let { fileMappingService.getMapping(it) }

        if (mapping == null) {
            val hash = path.bundleAttrs[FileAttribute.HASH]?.value() as String?
                ?: throw IllegalArgumentException("path must have hash attr")
            val type = path.bundleAttrs[FileAttribute.TYPE]?.value() as MediaType?
                ?: throw IllegalArgumentException("path must have type attr")
            if (hash.contains("/"))
                throw IllegalArgumentException("hash must not contain '/'")
            return virtualFileSystem.getPath(type.type, type.subtype, hash)
        }
        return virtualFileSystem.getPath(mapping.type, mapping.subType, mapping.hash).apply {
            bundleAttrs[FileAttribute.TYPE] = FileTypeAttribute(
                MediaType.parse("${mapping.type}/${mapping.subType}")
            )
        }
    }

    override fun fileExists(path: PubPath): Boolean {
        val id = virtualFileService.genId(path)
        return virtualFileService.exists(id)
    }

    @Transactional
    override fun createFolder(path: PubPath, owner: Uid) {
        virtualFileService.addFolder(owner, path.toAbsolutePath())
    }

    @Transactional
    override fun deleteFile(path: PubPath) {
        val vf = virtualFileService.get(path)
            ?: throw NoSuchFileException(path.pathString)
        if (vf.isFolder()) {
            virtualFileService.delete(path)
            return
        }
        val count = virtualFileService.getHandlerCount(
            vf.hash ?: throw IllegalStateException("hash is null")
        )
        if (count == 0)
            throw RuntimeException("No hash for path : $path")
        if (count == 1) {
            val fm = fileMappingService.getMapping(vf.hash!!)
                ?: throw IllegalStateException("hash not found: ${vf.hash}")
            val vp = virtualFileSystem.getPath("data", fm.type, fm.subType, fm.hash)
            if (fm.preview) {
                fileMappingService.getPreviewFile(fm)?.del()
            }
            val f = vp.toFile()
            virtualFileService.delete(path)
            fileMappingService.deleteMapping(vf.hash!!)
            f.del()
        } else {
            virtualFileService.delete(path)
        }
    }
}