package com.yjl.hnas.services

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.VFile
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.fs.attr.FileAttribute
import com.yjl.hnas.mapper.VFileMapper
import com.yjl.hnas.service.PubFileService
import com.yjl.hnas.service.VirtualFileService
import com.yjl.hnas.utils.timestamp
import org.apache.tika.mime.MediaType
import org.springframework.stereotype.Service
import java.nio.file.AccessMode
import kotlin.io.path.pathString

/**
 * @author YJL
 */
@Service
class PubFileServiceImpl(
    val vFileMapper: VFileMapper,
    val virtualFileService: VirtualFileService,
    virtualFileSystemProvider: VirtualFileSystemProvider,
) : PubFileService {

    val virtualFileSystem = virtualFileSystemProvider.getFileSystem()

    override fun checkAccess(path: PubPath, vararg modes: AccessMode) {

    }

    override fun toVirtualPath(path: PubPath): VirtualPath {
        val hash = path.bundleAttrs[FileAttribute.HASH]?.value() as String?
            ?: throw IllegalArgumentException("path must have hash attr")
        val type = path.bundleAttrs[FileAttribute.TYPE]?.value() as MediaType?
            ?: throw IllegalArgumentException("path must have type attr")
        if (hash.contains("/"))
            throw IllegalArgumentException("hash must not contain '/'")
        return virtualFileSystem.getPath("data", type.type, type.subtype, hash)
    }

    override fun folderExists(path: PubPath): Boolean {
        val id = virtualFileService.genPubId(path.toAbsolutePath().pathString)
        return vFileMapper.selectById(id) != null
    }

    override fun createFolder(path: PubPath, owner: Uid) {
        val p = path.toAbsolutePath()
        if (p.isRoot()) {
            val t = System.currentTimeMillis()
            vFileMapper.insert(
                VFile(
                    fid = virtualFileService.genPubId("/"),
                    name = "",
                    parent = null,
                    owner = 0,
                    createTime = t.timestamp,
                    updateTime = t.timestamp,
                    type = VFile.Type.FOLDER
                )
            )
            return
        }
        val pp = p.parent
        if (!folderExists(pp))
            createFolder(pp, owner)
        val time = System.currentTimeMillis()
        vFileMapper.insert(
            VFile(
                fid = virtualFileService.genPubId(p.pathString),
                name = p.fileName.toString(),
                parent = virtualFileService.genPubId(pp.pathString),
                owner = owner,
                createTime = time.timestamp,
                updateTime = time.timestamp,
                type = VFile.Type.FOLDER
            )
        )

    }
}