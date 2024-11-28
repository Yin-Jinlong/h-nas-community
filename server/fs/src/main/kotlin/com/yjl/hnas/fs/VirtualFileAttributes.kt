package com.yjl.hnas.fs

import com.yjl.hnas.entity.IVirtualFile
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime

/**
 * @author YJL
 */
class VirtualFileAttributes(
    private val vf: IVirtualFile
) : BasicFileAttributes {

    val creationFileTime: FileTime = FileTime.fromMillis(vf.createTime.time)
    val lastModifiedFileTime: FileTime = FileTime.fromMillis(vf.createTime.time)

    override fun lastModifiedTime() = lastModifiedFileTime

    override fun lastAccessTime() = lastModifiedFileTime

    override fun creationTime() = creationFileTime

    override fun isRegularFile() = vf.isFile()

    override fun isDirectory() = vf.isFolder()

    override fun isSymbolicLink() = false

    override fun isOther() = false

    override fun size() = vf.size

    override fun fileKey() = vf.fid

}