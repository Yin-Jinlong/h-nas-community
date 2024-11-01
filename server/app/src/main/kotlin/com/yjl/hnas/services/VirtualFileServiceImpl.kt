package com.yjl.hnas.services

import com.yjl.hnas.entity.FileMapping
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.VFile
import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.mapper.FileMappingMapper
import com.yjl.hnas.mapper.VFileMapper
import com.yjl.hnas.service.VirtualFileService
import com.yjl.hnas.service.virtual.VirtualFileSystemProvider
import com.yjl.hnas.service.virtual.VirtualPath
import io.github.yinjinlong.md.sha256
import org.springframework.stereotype.Service
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

    override fun createFolder(dir: VirtualPath, name: String) {
        if (dir.user == null)
            throw ErrorCode.NO_PERMISSION.data("创建文件夹需要登录")
        if (vFileMapper.selectById(dir.id) == null) {
            createFolder(dir.parent, dir.name)
        }
        val file = dir.resolve(name)
        vFileMapper.insert(
            VFile(
                fid = file.id,
                name = name,
                parent = dir.id,
                owner = dir.user!!,
                type = VFile.Type.FOLDER
            )
        )
    }
}