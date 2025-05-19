package io.github.yinjinlong.hnas.utils

import io.github.yinjinlong.hnas.data.FileInfo
import io.github.yinjinlong.hnas.entity.IVirtualFile
import io.github.yinjinlong.hnas.fs.VirtualPath
import io.github.yinjinlong.hnas.service.FileMappingService

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
        },
        owner = owner,
        extra = if (extra?.isJsonObject == true) extra!!.asJsonObject.apply {
            addProperty("path", dir.resolve(name).path)
        } else extra,
    )
}
