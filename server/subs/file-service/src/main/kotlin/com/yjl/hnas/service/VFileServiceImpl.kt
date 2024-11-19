package com.yjl.hnas.service

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.VFile
import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.mapper.VFileMapper
import com.yjl.hnas.utils.base64Url
import com.yjl.hnas.utils.reBase64Url
import com.yjl.hnas.utils.timestamp
import io.github.yinjinlong.md.sha256
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.io.path.name

/**
 * @author YJL
 */
@Service
class VFileServiceImpl(
    val vFileMapper: VFileMapper,
) : VFileService {

    override fun genId(access: String, path: String): VFileId {
        return "$access:$path".sha256.base64Url
    }

    override fun exists(id: VFileId): Boolean {
        return vFileMapper.selectById(id) != null
    }

    fun updateParentSize(dir: PubPath, size: Long) {
        val pvf = vFileMapper.selectById(genId(dir))
            ?: throw IllegalStateException("父目录不存在：$dir")
        vFileMapper.updateSize(pvf.fid, pvf.size + size)
        val p = dir.parent
        if (p.isRoot() && dir.isRoot())
            return
        updateParentSize(p, size)
    }

    @Transactional
    override fun addVFile(owner: Uid, path: PubPath, size: Long, hash: String) {
        val p = path.parent
        val time = System.currentTimeMillis().timestamp
        vFileMapper.insert(
            VFile(
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
            vFileMapper.insert(
                VFile(
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
        vFileMapper.insert(
            VFile(
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
        vFileMapper.deleteById(id)
    }

    @Transactional
    override fun delete(path: PubPath) {
        val id = genId(path)
        val vf = vFileMapper.selectById(id) ?: throw ErrorCode.NO_SUCH_FILE.error
        delete(id)
        updateParentSize(path.parent, -vf.size)
    }

    override fun getHandlerCount(hash: String): Int {
        return vFileMapper.countHash(hash)
    }

    override fun getById(id: VFileId): VFile? {
        return vFileMapper.selectById(id)
    }
}
