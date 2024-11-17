package com.yjl.hnas.entity.view

import com.yjl.hnas.entity.VFile
import com.yjl.hnas.entity.VFileId
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
vfile.hash,
vfile.name,
vfile.parent,
vfile.type as file_type,
ifnull(file_mapping.type,'folder') as type,
ifnull(file_mapping.sub_type,'folder') as sub_type,
vfile.create_time,
vfile.update_time
from vfile
    left join file_mapping on vfile.hash=file_mapping.hash
"""
)
@Entity
@Table
class VirtualFile {
    @Id
    var hash: VFileId = ""
    var name: String = ""
    var parent: VFileId? = null
    var fileType: VFile.Type = VFile.Type.FILE
    val type: String = ""
    val subType: String = ""
    val createTime: Timestamp = Timestamp(0)
    val updateTime: Timestamp = Timestamp(0)
}
