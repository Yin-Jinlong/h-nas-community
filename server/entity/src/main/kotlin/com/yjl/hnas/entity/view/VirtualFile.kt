package com.yjl.hnas.entity.view

import com.yjl.hnas.entity.VFile
import com.yjl.hnas.entity.VFileId
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.View

/**
 * @author YJL
 */
@View(
    query = """
select 
vfile.fid,
vfile.name,
vfile.parent,
vfile.type as file_type,
file_mapping.type,
file_mapping.sub_type,
vfile.create_time,
vfile.update_time
from vfile
    join file_mapping on vfile.fid=file_mapping.fid
"""
)
@Entity
@Table
class VirtualFile {
    @Id
    var fid: VFileId = ""
    var name: String = ""
    var parent: VFileId? = null
    var fileType: VFile.Type = VFile.Type.FILE
    val type: String = ""
    val subType: String = ""
    val createTime: Long = 0
    val updateTime: Long = 0
}
