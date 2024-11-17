package com.yjl.hnas.services

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.VFile
import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.fs.PubPath
import com.yjl.hnas.mapper.VFileMapper
import com.yjl.hnas.service.VFileService
import com.yjl.hnas.utils.base64Url
import com.yjl.hnas.utils.reBase64Url
import com.yjl.hnas.utils.timestamp
import io.github.yinjinlong.md.sha256
import org.springframework.stereotype.Service
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

    override fun addVFile(owner: Uid, path: PubPath, hash: String) {
        val p = path.parent
        val time = System.currentTimeMillis().timestamp
        vFileMapper.insert(
            VFile(
                fid = genId(path),
                hash = hash.reBase64Url,
                name = path.name,
                parent = genId(p),
                owner = owner,
                createTime = time,
                updateTime = time,
                type = VFile.Type.FILE
            )
        )
    }

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
                    type = VFile.Type.FOLDER
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
                type = VFile.Type.FOLDER
            )
        )
    }

    override fun delete(id: VFileId) {
        vFileMapper.deleteById(id)
    }

    override fun getHandlerCount(hash: String): Int {
        return vFileMapper.countHash(hash)
    }

    override fun getById(id: VFileId): VFile? {
        return vFileMapper.selectById(id)
    }
}
