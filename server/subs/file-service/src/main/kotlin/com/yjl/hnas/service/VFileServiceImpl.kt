package com.yjl.hnas.service

import com.yjl.hnas.entity.*
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.mapper.VirtualFileMapper
import com.yjl.hnas.utils.base64Url
import com.yjl.hnas.utils.reBase64Url
import com.yjl.hnas.utils.timestamp
import io.github.yinjinlong.md.sha256
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.nio.file.AccessMode
import kotlin.io.path.name

/**
 * @author YJL
 */
@Service
class VFileServiceImpl(
    val fileMappingService: FileMappingService,
    val virtualFileMapper: VirtualFileMapper,
) : VirtualFileService {

    override fun checkAccess(path: VirtualPath, vararg modes: AccessMode) {

    }

    override fun convertToFile(path: VirtualPath): File {
        return File("data", path.path)
    }

    override fun genId(access: String, path: String): VFileId {
        return "$access:$path".sha256.base64Url
    }

    override fun exists(id: VFileId): Boolean {
        return virtualFileMapper.selectById(id) != null
    }

    fun updateParentSize(dir: PubPath, size: Long) {
        val pvf = virtualFileMapper.selectById(genId(dir))
            ?: throw IllegalStateException("父目录不存在：$dir")
        virtualFileMapper.updateSize(pvf.fid, pvf.size + size)
        val p = dir.parent
        if (p.isRoot() && dir.isRoot())
            return
        updateParentSize(p, size)
    }

    @Transactional
    override fun addVFile(owner: Uid, path: PubPath, size: Long, hash: String) {
        val p = path.parent
        val time = System.currentTimeMillis().timestamp
        virtualFileMapper.insert(
            VirtualFile(
                fid = genId(path),
                name = path.name,
                parent = genId(p),
                hash = hash.reBase64Url,
                owner = owner,
                createTime = time,
                updateTime = time,
                size = size
            )
        )
        updateParentSize(p, size)
    }

    @Transactional
    override fun addFolder(owner: Uid, path: PubPath) {
        if (exists(path))
            return
        if (path.isRoot()) {
            val t = System.currentTimeMillis()
            virtualFileMapper.insert(
                VirtualFile(
                    fid = genId(path),
                    name = "",
                    parent = null,
                    owner = 0,
                    createTime = t.timestamp,
                    updateTime = t.timestamp,
                )
            )
            return
        }
        val pp = path.parent
        if (!exists(pp))
            addFolder(owner, pp)
        val time = System.currentTimeMillis()
        virtualFileMapper.insert(
            VirtualFile(
                fid = genId(path),
                name = path.name,
                parent = genId(pp),
                owner = owner,
                createTime = time.timestamp,
                updateTime = time.timestamp,
            )
        )
    }

    fun delete(id: VFileId) {
        virtualFileMapper.deleteById(id)
    }

    @Transactional
    override fun delete(path: PubPath) {
        val id = genId(path)
        val vf = virtualFileMapper.selectById(id) ?: throw ErrorCode.NO_SUCH_FILE.error
        delete(id)
        updateParentSize(path.parent, -vf.size)
    }

    override fun getHandlerCount(hash: String): Int {
        return virtualFileMapper.countHash(hash)
    }

    override fun getById(id: VFileId): IVirtualFile? {
        return virtualFileMapper.selectById(id)
    }

    override fun getByParent(parent: VFileId): List<IVirtualFile> {
        return virtualFileMapper.selectsByParent(parent)
    }

    @Transactional
    override fun createPubFile(
        owner: Uid,
        path: PubPath,
        size: Long,
        hash: String
    ) {
        if (exists(path))
            throw ErrorCode.FILE_EXISTS.data(path)
        if (!exists(path.parent))
            addFolder(owner, path.parent)
        addVFile(owner, path, size, hash)
        if (fileMappingService.getMapping(hash) == null)
            fileMappingService.addMapping(path, size, hash)
    }
}
