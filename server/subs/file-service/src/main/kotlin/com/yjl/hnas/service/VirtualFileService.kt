package com.yjl.hnas.service

import com.yjl.hnas.data.AudioFileInfo
import com.yjl.hnas.data.FileRange
import com.yjl.hnas.entity.ChildrenCount
import com.yjl.hnas.entity.Hash
import com.yjl.hnas.entity.IVirtualFile
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.fs.VirtualFileManager
import com.yjl.hnas.fs.VirtualPath
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.NoSuchFileException
import java.nio.file.NotDirectoryException

/**
 * @author YJL
 */
interface VirtualFileService : VirtualFileManager {

    /**
     * 判断路径文件是否存在
     */
    fun exists(path: VirtualPath): Boolean

    /**
     * 生成路径id
     */
    fun genId(path: VirtualPath): Hash

    /**
     * 获取文件信息
     */
    fun get(path: VirtualPath): IVirtualFile?

    /**
     * 获取父级文件夹下所有文件
     */
    @Throws(NoSuchFileException::class)
    fun getByParent(parent: VirtualPath, type: String?): List<IVirtualFile>

    /**
     * 上传文件
     */
    @Throws(
        FileAlreadyExistsException::class,
        IllegalArgumentException::class,
        NoSuchFileException::class,
        IOException::class
    )
    fun upload(
        owner: Uid,
        path: VirtualPath,
        hash: Hash,
        fileSize: Long,
        range: FileRange,
        ins: BufferedInputStream
    ): Boolean

    /**
     * 重命名文件，只能重命名文件名，不能改父级
     */
    @Throws(
        NoSuchFileException::class,
        IOException::class
    )
    fun rename(path: VirtualPath, name: String)

    /**
     * 获取文件夹下文件数量
     */
    @Throws(
        NotDirectoryException::class
    )
    fun getFolderChildrenCount(path: VirtualPath): ChildrenCount

    /**
     * 获取音频信息
     */
    fun getAudioInfo(path: VirtualPath): AudioFileInfo

    /**
     * 获取音频封面
     */
    fun getAudioCover(path: VirtualPath): File
}
