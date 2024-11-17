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


/*fun FileMapping(
    folderService: FolderService, path: String, res: Resource
): FileMapping {
    val md5 = getMd5(res.inputStream)
    val mime = MediaType.parse(res.inputStream.mimeType)
    return FileMapping(
        FileMapping.PK(
            path.split("/").last(),
            folder = folderService.getFolderByPath(path)?.id ?: 0,
        ),
        dataPath = getDataPath(mime, md5),
        md5 = md5,
        type = mime.type,
        subType = mime.subtype
    )
}*/

/*fun FileMapping.toFileInfo(folderService: FolderService): FileInfo {
    val file = toFile()
    return FileInfo(
        folderService.getPath(pk.folder) + pk.name,
        dataPath,
        type,
        if (canPreview()) "/api/thumbnail/${pk.name}"
        else null,
        file.lastModified(),
        file.length()
    )
}*/
