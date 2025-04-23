package io.github.yinjinlong.hnas.service

import io.github.yinjinlong.hnas.data.AudioFileInfo
import io.github.yinjinlong.hnas.data.FileRange
import io.github.yinjinlong.hnas.entity.ChildrenCount
import io.github.yinjinlong.hnas.fs.VirtualFileManager
import io.github.yinjinlong.hnas.fs.VirtualPath
import io.github.yinjinlong.hnas.entity.FileId
import io.github.yinjinlong.hnas.entity.Hash
import io.github.yinjinlong.hnas.entity.IVirtualFile
import io.github.yinjinlong.hnas.entity.Uid
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
     * 获取id
     */
    fun getId(name: String, parent: FileId): Hash?

    /**
     * 获取根目录id，目录不存在会自动创建
     */
    fun getRootId(user: Uid): Hash

    /**
     * 获取文件信息
     */
    fun get(path: VirtualPath): IVirtualFile?

    /**
     * 获取父级目录下所有文件
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
     * 获取目录下文件数量
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
