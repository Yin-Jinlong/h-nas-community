package com.yjl.hnas.entity.view

import com.yjl.hnas.entity.IVFile
import com.yjl.hnas.entity.VFileId
import java.sql.Timestamp

/**
 *
 * @author YJL
 */
interface IVirtualFile {
    var fid: VFileId
    var hash: VFileId
    var name: String
    var parent: VFileId?
    val fileType: IVFile.Type
        get() = if (type == "folder") IVFile.Type.FOLDER else IVFile.Type.FILE
    var type: String
    var subType: String
    val createTime: Timestamp
    val updateTime: Timestamp
    var size: Long
    var preview: Boolean
    var dataPath: String?
}
