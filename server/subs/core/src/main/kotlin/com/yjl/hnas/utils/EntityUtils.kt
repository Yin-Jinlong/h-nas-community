package com.yjl.hnas.utils

import com.yjl.hnas.data.FileInfo
import com.yjl.hnas.entity.IVFile
import com.yjl.hnas.entity.view.IVirtualFile
import com.yjl.hnas.service.FileMappingService

fun IVirtualFile.toFileInfo(fileMappingService: FileMappingService): FileInfo {
    return FileInfo(
        name = name,
        fileType = fileType,
        type = type,
        subType = subType,
        preview = null,
        createTime = createTime.time,
        updateTime = updateTime.time,
        size = if (fileType == IVFile.Type.FOLDER) {
            size.also {
                if (it < 0)
                    throw IllegalStateException("文件夹没有大小：$fid")
            }
        } else size.let {
            if (it == -1L) {
                fileMappingService.getSize(hash)
            } else
                it
        }
    )
}
