package com.yjl.hnas.service

import com.yjl.hnas.entity.*
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.fs.VirtualFilesystem
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.mapper.AudioInfoMapper
import com.yjl.hnas.mapper.ChildrenCountMapper
import com.yjl.hnas.mapper.VirtualFileMapper
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
        get() = pathIdOrThrow(this)

    override fun exists(path: VirtualPath): Boolean {
        return pathId(path) != null
    }

    override fun onBind(fsp: VirtualFileSystemProvider) {
        fs = fsp.virtualFilesystem
    }

    override fun getId(name: String, parent: FileId): Hash? {
        return virtualFileMapper.selectIdByNameParent(name, parent)
    }

    override fun getRootId(user: Uid): Hash {
        val id = virtualFileMapper.selectRootIdByUser(user)
        if (id != null)
            return id
        virtualFileMapper.insert(
            VirtualFile(
                name = "",
                user = user,
                owner = user,
            )
        )
        return (virtualFileMapper.selectRootIdByUser(user)
            ?: throw IllegalStateException("Root id get Failed : $user")).also {
            childrenCountMapper.insert(it)
        }
    }

    /**
     * 获取path对应的id，不存在则返回null
     */
    protected fun pathId(path: VirtualPath): FileId? {
        var id: FileId? = getRootId(path.user() ?: 0L)
        for (name in path.toAbsolutePath().names) {
            id = getId(name, id ?: return null)
        }
        return id
    }

    /**
     * 获取path对应的id，不存在则抛出异常
     */
    protected fun pathIdOrThrow(path: VirtualPath): FileId {
        return pathId(path) ?: throw NoSuchFileException(path.fullPath)
    }

    fun insertAndGetId(vf: VirtualFile): FileId {
        virtualFileMapper.insert(vf)
        return virtualFileMapper.selectIdByNameParent(vf.name, vf.parent)
            ?: throw IllegalStateException("Insert Failed : $vf")
    }

    override fun get(path: VirtualPath): IVirtualFile? {
        return pathId(path)?.let { virtualFileMapper.selectById(it) }
    }

    protected fun getOrThrow(path: VirtualPath): IVirtualFile {
        return get(path) ?: throw NoSuchFileException(path.fullPath)
    }

    override fun getByParent(parent: VirtualPath, type: String?): List<IVirtualFile> {
        val id = pathIdOrThrow(parent.toAbsolutePath())
        return virtualFileMapper.selectsByParent(id)
    }

    override fun getFolderChildrenCount(path: VirtualPath): ChildrenCount {
        val id = pathIdOrThrow(path)
        return childrenCountMapper.selectByFid(id)
            ?: throw NotDirectoryException(path.fullPath)
    }

    protected fun getAudio(vf: IVirtualFile): AudioInfo? {
        return audioInfoMapper.selectByHash(vf.hash ?: throw ErrorCode.BAD_FILE_FORMAT.error)
    }
}