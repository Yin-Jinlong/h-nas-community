package com.yjl.hnas.services

import com.yjl.hnas.entity.*
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.mapper.FileMappingMapper
import com.yjl.hnas.mapper.PublicVFileMapper
import com.yjl.hnas.mapper.VFileMapper
import com.yjl.hnas.service.VirtualFileService
import com.yjl.hnas.service.virtual.VirtualFileSystemProvider
import com.yjl.hnas.service.virtual.VirtualPath
import com.yjl.hnas.utils.timestamp
import io.github.yinjinlong.md.sha256
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.io.path.absolutePathString
import kotlin.io.path.name

/**
 * @author YJL
 */
@Service
class VirtualFileServiceImpl(
    val vFileMapper: VFileMapper,
    val publicVFileMapper: PublicVFileMapper,
    val fileMappingMapper: FileMappingMapper,
) : VirtualFileService {

    private val virtualFileSystemProvider = VirtualFileSystemProvider()
    private val virtualFileSystem = virtualFileSystemProvider.getFileSystem(null)

    @OptIn(ExperimentalEncodingApi::class)
    override fun getVFileId(uid: Uid?, path: String): String {
        return Base64.encode("${uid ?: ""}:$path".sha256)
    }

    override fun getVFileId(virtualPath: VirtualPath): String {
        return getVFileId(virtualPath.user, virtualPath.absolutePathString())
    }

    override fun toVirtualPath(uid: Uid?, path: String): VirtualPath {
        return virtualFileSystem.getPath(uid?.toString() ?: "", path)
    }

    override fun getVFile(path: VirtualPath): VFile {
        return vFileMapper.selectById(getVFileId(path))
            ?: throw ErrorCode.NO_SUCH_FILE(path.toFullString())
    }

    override fun getFileMapping(fid: VFileId): FileMapping? {
        return fileMappingMapper.selectById(fid)
    }

    override fun getFileMapping(path: VirtualPath): FileMapping {
        return getFileMapping(getVFileId(path))
            ?: throw ErrorCode.NO_SUCH_FILE(path.toFullString())
    }

    override fun getFiles(path: VirtualPath): List<VFile> {
        return vFileMapper.selectsByParent(getVFileId(path))
    }

    @Transactional
    override fun createFolder(dir: VirtualPath, name: String, owner: Uid, public: Boolean) {
        if (public) {
            require(dir.user == null) { "public dir user must be null" }
        }
        require(dir.user == null || dir.user == owner) { "dir.user != owner" }
        if (dir.isRoot() && name.isEmpty()) {
            val time = System.currentTimeMillis()
            vFileMapper.insert(
                VFile(
                    fid = dir.id,
                    name = "",
                    parent = null,
                    owner = if (public) 0 else owner,
                    createTime = time.timestamp,
                    updateTime = time.timestamp,
                    type = VFile.Type.FOLDER
                )
            )
            if (public) {
                publicVFileMapper.insert(
                    PublicVFile(
                        fid = dir.id,
                    )
                )
            }
            return
        }
        if (vFileMapper.selectById(dir.id) == null) {
            createFolder(dir.parent, dir.name, owner, public)
        }
        val file = dir.resolve(name)
        val time = System.currentTimeMillis()
        vFileMapper.insert(
            VFile(
                fid = file.id,
                name = name,
                parent = dir.id,
                owner = owner,
                createTime = time.timestamp,
                updateTime = time.timestamp,
                type = VFile.Type.FOLDER
            )
        )
        if (public) {
            publicVFileMapper.insert(
                PublicVFile(
                    fid = file.id,
                )
            )
        }
    }
}