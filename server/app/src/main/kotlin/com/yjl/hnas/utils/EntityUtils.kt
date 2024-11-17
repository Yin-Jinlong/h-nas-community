package com.yjl.hnas.utils

import com.yjl.hnas.data.FileInfo
import com.yjl.hnas.entity.VFile
import com.yjl.hnas.entity.view.VirtualFile
import com.yjl.hnas.service.FileMappingService

fun VirtualFile.toFileInfo(fileMappingService: FileMappingService): FileInfo {
    return FileInfo(
        name = name,
        fileType = fileType,
        type = type,
        subType = subType,
        preview = null,
        createTime = createTime.time,
        updateTime = updateTime.time,
        size = if (fileType == VFile.Type.FOLDER) -1 else size.let {
            if (it == -1L) {
                fileMappingService.getSize(hash)
            } else
                it
        }
    )
}
