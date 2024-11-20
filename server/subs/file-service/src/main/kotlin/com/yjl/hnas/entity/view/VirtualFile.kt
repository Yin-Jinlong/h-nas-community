package com.yjl.hnas.entity.view

import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.fs.VirtualFileSystem
import com.yjl.hnas.fs.VirtualPath
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.View
import java.sql.Timestamp

/**
 * @author YJL
 */
@View(
    query = """
select 
vfile.fid,
vfile.hash,
vfile.name,
vfile.parent,
ifnull(file_mapping.type,'folder') as type,
ifnull(file_mapping.sub_type,'folder') as sub_type,
vfile.create_time,
vfile.update_time,
vfile.size,
file_mapping.data_path
from vfile
    left join file_mapping on vfile.hash=file_mapping.hash
"""
)
@Entity
@Table
class VirtualFile : IVirtualFile {
    @Id
    override var fid: VFileId = ""
    override var hash: VFileId = ""
    override var name: String = ""
    override var parent: VFileId? = null
    override var type: String = ""
    override var subType: String = ""
    override val createTime: Timestamp = Timestamp(0)
    override val updateTime: Timestamp = Timestamp(0)
    override var size: Long = -1
    override var dataPath: String? = null

    fun toVirtualPath(virtualFileSystem: VirtualFileSystem): VirtualPath {
        return virtualFileSystem.getPath(dataPath ?: throw IllegalStateException("no data path"))
    }
}
