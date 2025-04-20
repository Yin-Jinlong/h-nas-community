package com.yjl.hnas.utils

import com.yjl.hnas.data.FileInfo
import com.yjl.hnas.entity.IVirtualFile
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.service.FileMappingService

fun IVirtualFile.toFileInfo(
    dir: VirtualPath,
    fileMappingService: FileMappingService
): FileInfo {
    return FileInfo(
        name = name,
        dir = dir.path,
        fileType = type,
        mediaType = hash?.let { fileMappingService.getMediaType(it) },
        createTime = createTime.time,
        updateTime = updateTime.time,
        size = if (type == IVirtualFile.Type.FOLDER) {
            size.also {
                if (it < 0)
                    throw IllegalStateException("目录没有大小：$fid")
            }
        } else size.let {
            if (it == -1L) {
                fileMappingService.getSize(hash!!)
            } else
                it
        }
    )
}
