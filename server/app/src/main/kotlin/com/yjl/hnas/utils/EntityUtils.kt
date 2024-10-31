package com.yjl.hnas.utils

import com.yjl.hnas.service.virtual.VirtualFile
import com.yjl.hnas.service.virtual.VirtualPath
import com.yjl.hnas.entity.VFile

fun VFile.virtual(dir: VirtualPath): VirtualFile {
    return VirtualFile(dir.resolve(name), this)
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
