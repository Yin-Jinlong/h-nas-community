package com.yjl.hnas.utils

import com.yjl.hnas.data.FileInfo
import com.yjl.hnas.entity.IVFile
import com.yjl.hnas.entity.view.IVirtualFile
import com.yjl.hnas.preview.PreviewGeneratorFactory
import com.yjl.hnas.service.FileMappingService
import org.apache.tika.mime.MediaType

fun IVirtualFile.toFileInfo(
    previewGeneratorFactory: PreviewGeneratorFactory,
    fileMappingService: FileMappingService
): FileInfo {
    val mediaType = MediaType.parse("$type/$subType")
    return FileInfo(
        name = name,
        fileType = fileType,
        type = type,
        subType = subType,
        preview = previewGeneratorFactory.canPreview(mediaType),
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
