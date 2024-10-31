package com.yjl.hnas.entity.view

import com.yjl.hnas.entity.FileMapping
import com.yjl.hnas.entity.FileWithType
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
@Entity
@Table(name = "public_vfiles")
@View(
    query = """
select 
    vfile.fid, 
    vfile.type as file_type,
    file_mapping.hash as hash,
    file_mapping.data_path,
    file_mapping.type,
    file_mapping.sub_type,
    vfile.create_time,
    vfile.update_time
    from public_vfile 
    join vfile on vfile.fid=public_vfile.fid
    join file_mapping on vfile.fid=file_mapping.fid
"""
)
data class PublicVFileInfo(

    @Id
    var fid: VFileId = "",

    var fileType: VFile.Type = VFile.Type.FILE,

    var hash: String = "",

    var dataPath: String = "",

    override var type: String = "",

    override var subType: String = "",

    var createTime: Timestamp = Timestamp(0),

    var updateTime: Timestamp = Timestamp(0),
) : FileWithType {

    fun toFileMapping() = FileMapping(fid, hash, hash, type, subType)
}