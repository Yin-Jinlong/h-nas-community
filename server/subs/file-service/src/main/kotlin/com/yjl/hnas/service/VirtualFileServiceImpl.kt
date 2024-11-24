package com.yjl.hnas.service

import com.yjl.hnas.entity.IVirtualFile
import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.VirtualFile
import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.fs.VirtualFilesystem
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.fs.attr.FileAttributes
import com.yjl.hnas.mapper.VirtualFileMapper
import com.yjl.hnas.utils.base64Url
import com.yjl.hnas.utils.timestamp
import io.github.yinjinlong.md.sha256
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.nio.channels.SeekableByteChannel
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import kotlin.io.path.name

/**
 * @author YJL
 */
@Service
class VirtualFileServiceImpl(
    val virtualFileMapper: VirtualFileMapper,
) : VirtualFileService {

    private lateinit var fs: VirtualFilesystem

    private val VirtualPath.id: String
        get() = genId(this.toAbsolutePath())

    override fun exists(path: VirtualPath): Boolean {
        return virtualFileMapper.selectById(path.id) != null
    }

    override fun onBind(fsp: VirtualFileSystemProvider) {
        fs = fsp.virtualFilesystem
    }

    override fun newByteChannel(
        path: VirtualPath,
        options: MutableSet<out OpenOption>,
        attrs: Map<String, FileAttribute<*>>
    ): SeekableByteChannel {
        TODO("Not yet implemented")
    }

    override fun genId(path: VirtualPath): String {
        return path.toAbsolutePath().fullPath.sha256.base64Url
    }

    override fun get(path: VirtualPath): IVirtualFile? {
        return virtualFileMapper.selectById(path.id)
    }

    override fun getByParent(parent: VirtualPath): List<IVirtualFile> {
        val p = parent.toAbsolutePath()
        if (!exists(p) && !p.isRoot)
            throw NoSuchFileException(p.fullPath)
        return virtualFileMapper.selectsByParent(p.id)
    }

    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRES_NEW)
    protected fun insertDir(owner: Uid, dir: VirtualPath) {
        if (dir.isRoot) {
            val time = System.currentTimeMillis().timestamp
            virtualFileMapper.insert(
                VirtualFile(
                    fid = dir.id,
                    name = "",
                    parent = "",
                    hash = null,
                    createTime = time,
                    updateTime = time,
                )
            )
            return
        }

        val p = dir.parent
        if (!exists(p))
            insertDir(owner, p)

        if (p same dir)
            return
        val time = System.currentTimeMillis().timestamp
        virtualFileMapper.insert(
            VirtualFile(
                fid = dir.id,
                name = dir.name,
                parent = p.id,
                hash = null,
                owner = owner,
                createTime = time,
                updateTime = time,
            )
        )
    }

    @Transactional(rollbackFor = [Exception::class])
    fun mkdirs(owner: Uid, dir: VirtualPath) {
        if (exists(dir))
            throw FileAlreadyExistsException(dir.fullPath)
        insertDir(owner, dir)
    }

    override fun newDirectoryStream(
        dir: VirtualPath,
        filter: DirectoryStream.Filter<in VirtualPath>
    ): DirectoryStream<VirtualPath> {
        TODO("Not yet implemented")
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun createDirectory(dir: VirtualPath, attrs: Map<String, FileAttribute<*>>) {
        val owner = attrs[FileAttributes.OWNER]
            ?: throw IllegalArgumentException("owner is null: $dir")
        mkdirs(owner.value() as Uid, dir.toAbsolutePath())
    }

    override fun delete(path: VirtualPath) {
        TODO("Not yet implemented")
    }

    override fun copy(source: VirtualPath, target: VirtualPath, options: Set<CopyOption>) {
        TODO("Not yet implemented")
    }

    override fun move(source: VirtualPath, target: VirtualPath, options: Set<CopyOption>) {
        TODO("Not yet implemented")
    }

    override fun isSameFile(path: VirtualPath, path2: VirtualPath): Boolean {
        TODO("Not yet implemented")
    }

    override fun isHidden(path: VirtualPath): Boolean {
        TODO("Not yet implemented")
    }

    override fun getFileStore(path: VirtualPath): FileStore {
        TODO("Not yet implemented")
    }

    override fun checkAccess(path: VirtualPath, modes: Set<AccessMode>) {
        if (!exists(path))
            throw NoSuchFileException(path.fullPath)
        if (AccessMode.EXECUTE in modes)
            throw AccessDeniedException(path.fullPath)
    }

    override fun <V : FileAttributeView> getFileAttributeView(
        path: VirtualPath,
        type: Class<V>,
        options: Set<LinkOption>
    ): V? {
        return null
    }

    override fun <A : BasicFileAttributes> readAttributes(
        path: VirtualPath,
        type: Class<A>,
        options: Set<LinkOption>
    ): A {
        TODO("Not yet implemented")
    }

    override fun readAttributes(
        path: VirtualPath,
        attributes: String,
        options: Set<LinkOption>
    ): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun setAttribute(path: VirtualPath, attribute: String, value: Any?, options: Set<LinkOption>) {
        throw UnsupportedOperationException()
    }
}
