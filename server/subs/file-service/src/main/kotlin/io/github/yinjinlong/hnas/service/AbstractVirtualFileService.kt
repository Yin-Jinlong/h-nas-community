package io.github.yinjinlong.hnas.service

import com.google.gson.JsonElement
import io.github.yinjinlong.hnas.entity.*
import io.github.yinjinlong.hnas.fs.VirtualFileSystemProvider
import io.github.yinjinlong.hnas.fs.VirtualFilesystem
import io.github.yinjinlong.hnas.fs.VirtualPath
import io.github.yinjinlong.hnas.mapper.ChildrenCountMapper
import io.github.yinjinlong.hnas.mapper.FileMappingMapper
import io.github.yinjinlong.hnas.mapper.VirtualFileMapper
import io.github.yinjinlong.hnas.utils.dbRecordNotFound
import java.nio.file.NoSuchFileException
import java.nio.file.NotDirectoryException
import java.util.*


/**
 * 处理基本业务层，不涉及path相关操作
 * @author YJL
 */
abstract class AbstractVirtualFileService(
    protected val virtualFileMapper: VirtualFileMapper,
    protected val childrenCountMapper: ChildrenCountMapper,
) : VirtualFileService {

    protected lateinit var fs: VirtualFilesystem

    protected val VirtualPath.id: Hash
        get() = pathIdOrThrow(this)

    protected val VirtualFile.path: VirtualPath
        get() {
            val names = LinkedList<String>()
            names.add(name)
            var f = this
            while (!f.parent.isZero) {
                val vf = virtualFileMapper.selectById(f.parent)
                    ?: throw IllegalStateException("Parent id not found : ${f.parent}")
                if (vf.name.isNotEmpty())
                    names.add(0, vf.name)
                f = vf
            }
            return if (user == 0L)
                fs.getPubPath(*names.toTypedArray())
            else
                fs.getUserPath(user, *names.toTypedArray())
        }

    override val table = VirtualFileMapper.TABLE

    protected fun mappingNotfound(hash: Hash?): Nothing = dbRecordNotFound(FileMappingMapper.TABLE, hash)

    protected fun childrenCountNotfound(dir: VirtualPath?): Nothing = dbRecordNotFound(ChildrenCountMapper.TABLE, dir)

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

    abstract fun getExtra(file: VirtualFile): JsonElement?

    override fun get(path: VirtualPath): IVirtualFile? {
        return pathId(path)?.let {
            virtualFileMapper.selectById(it)?.also { vf ->
                if (vf.extra == null) {
                    vf.extra = getExtra(vf)
                }
            }
        }
    }

    protected fun getOrThrow(path: VirtualPath): IVirtualFile {
        return get(path) ?: throw NoSuchFileException(path.fullPath)
    }

    override fun getByParent(parent: VirtualPath, type: String?): List<IVirtualFile> {
        val id = pathIdOrThrow(parent.toAbsolutePath())
        return virtualFileMapper.selectListByParent(id)
    }

    override fun getFolderChildrenCount(path: VirtualPath): ChildrenCount {
        val id = pathIdOrThrow(path)
        return childrenCountMapper.selectByFid(id)
            ?: throw NotDirectoryException(path.fullPath)
    }

}