package com.yjl.hnas.service.virtual

import com.yjl.hnas.data.FileInfo
import com.yjl.hnas.entity.FileMapping
import com.yjl.hnas.entity.VFile
import java.io.File
import kotlin.io.path.pathString

/**
 * @author YJL
 */
class VirtualFile private constructor(
    private val virtualPath: VirtualPath,
    private val vFile: VFile,
    private val mapping: FileMapping?,
) : File(mapping?.dataPath ?: "") {

    constructor(virtualPath: VirtualPath) :
            this(
                virtualPath.toAbsolutePath(),
                VirtualFileSystem.virtualFileService.getVFile(virtualPath)
            )

    constructor(virtualPath: VirtualPath, vFile: VFile) :
            this(
                virtualPath.toAbsolutePath(),
                vFile,
                if (vFile.isFile()) VirtualFileSystem.virtualFileService
                    .getFileMapping(virtualPath)
                else null
            ) {
        if (vFile.isFile())
            require(vFile.fid == mapping?.fid) { "文件id不匹配" }
    }

    fun toFileInfo(): FileInfo {
        return FileInfo(
            path = virtualPath.pathString,
            fileType = vFile.type,
            type = mapping?.type ?: "folder",
            subType = mapping?.subType ?: "folder",
            preview = if (mapping?.canPreview() == true) "/api/thumbnail/${mapping.hash}" else null,
            createTime = vFile.createTime.time,
            updateTime = vFile.updateTime.time,
            size = length(),
        )
    }

}
