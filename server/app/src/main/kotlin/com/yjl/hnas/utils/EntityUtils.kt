package com.yjl.hnas.utils

import com.yjl.hnas.data.FileInfo
import com.yjl.hnas.entity.view.VirtualFile
import com.yjl.hnas.fs.VirtualablePath
import kotlin.io.path.pathString

fun VirtualFile.toFileInfo(dir: VirtualablePath<*, *, *>): FileInfo {
    return FileInfo(
        path = dir.resolve(name).pathString,
        fileType = fileType,
        type = type,
        subType = subType,
        preview = null,
        createTime = createTime.time,
        updateTime = updateTime.time,
        size = 0
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
