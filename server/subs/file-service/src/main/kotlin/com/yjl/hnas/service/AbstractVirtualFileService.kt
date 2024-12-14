package com.yjl.hnas.service

import com.yjl.hnas.entity.AudioInfo
import com.yjl.hnas.entity.ChildrenCount
import com.yjl.hnas.entity.Hash
import com.yjl.hnas.entity.IVirtualFile
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.fs.VirtualFilesystem
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.mapper.AudioInfoMapper
import com.yjl.hnas.mapper.ChildrenCountMapper
import com.yjl.hnas.mapper.VirtualFileMapper
import com.yjl.hnas.utils.isAudioMediaType
import io.github.yinjinlong.md.sha256
import java.nio.file.NoSuchFileException
import java.nio.file.NotDirectoryException

/**
 * 处理基本业务层，不涉及path相关操作
 * @author YJL
 */
abstract class AbstractVirtualFileService(
    protected val virtualFileMapper: VirtualFileMapper,
    protected val childrenCountMapper: ChildrenCountMapper,
    protected val audioInfoMapper: AudioInfoMapper,
) : VirtualFileService {

    protected lateinit var fs: VirtualFilesystem

    protected val VirtualPath.id: Hash
        get() = genId(this.toAbsolutePath())

    override fun exists(path: VirtualPath): Boolean {
        return virtualFileMapper.selectById(path.id) != null
    }

    override fun onBind(fsp: VirtualFileSystemProvider) {
        fs = fsp.virtualFilesystem
    }

    override fun genId(path: VirtualPath): Hash {
        return Hash(path.toAbsolutePath().fullPath.sha256)
    }

    override fun get(path: VirtualPath): IVirtualFile? {
        return virtualFileMapper.selectById(path.id)
    }

    protected fun getOrThrow(path: VirtualPath): IVirtualFile {
        return virtualFileMapper.selectById(path.id)
            ?: throw NoSuchFileException(path.fullPath)
    }

    override fun getByParent(parent: VirtualPath, type: String?): List<IVirtualFile> {
        val p = parent.toAbsolutePath()
        if (!exists(p) && !p.isRoot)
            throw NoSuchFileException(p.fullPath)
        return virtualFileMapper.selectsByParent(p.id)
    }

    override fun getFolderChildrenCount(path: VirtualPath): ChildrenCount {
        return childrenCountMapper.selectByFid(path.id)
            ?: throw NotDirectoryException(path.fullPath)
    }

    protected fun checkAudio(vf: IVirtualFile): AudioInfo? {
        if (!vf.mediaType.isAudioMediaType)
            throw ErrorCode.BAD_FILE_FORMAT.error
        return audioInfoMapper.selectByHash(vf.hash ?: throw ErrorCode.BAD_FILE_FORMAT.error)
    }
}