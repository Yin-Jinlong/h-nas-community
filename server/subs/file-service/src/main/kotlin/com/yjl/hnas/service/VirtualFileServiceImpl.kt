package com.yjl.hnas.service

import com.yjl.hnas.data.FileRange
import com.yjl.hnas.data.UserInfo
import com.yjl.hnas.entity.*
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.VirtualFileSystemProvider
import com.yjl.hnas.fs.VirtualFilesystem
import com.yjl.hnas.fs.VirtualPath
import com.yjl.hnas.fs.attr.FileAttributes
import com.yjl.hnas.mapper.FileMappingMapper
import com.yjl.hnas.mapper.VirtualFileMapper
import com.yjl.hnas.preview.PreviewGeneratorFactory
import com.yjl.hnas.tika.FileDetector
import com.yjl.hnas.utils.del
import com.yjl.hnas.utils.mkParent
import com.yjl.hnas.utils.timestamp
import io.github.yinjinlong.md.sha256
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
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
    val fileMappingMapper: FileMappingMapper,
    private val previewGeneratorFactory: PreviewGeneratorFactory,
) : VirtualFileService {

    private lateinit var fs: VirtualFilesystem

    private val VirtualPath.id: Hash
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

    override fun genId(path: VirtualPath): Hash {
        return Hash(path.toAbsolutePath().fullPath.sha256)
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

    fun tmpFile(user: UserInfo, hash: Hash): File {
        return FileMappingService.dataFile("tmp/${user.uid}/$hash.tmp")
    }

    fun dataFile(hash: Hash): File {
        return FileMappingService.dataDataFile(hash.pathSafe)
    }

    @Transactional(rollbackFor = [Exception::class])
    fun updateParentSize(path: VirtualPath, ds: Long) {
        val vf = virtualFileMapper.selectById(path.id)
            ?: throw IllegalStateException("数据库文件不存在: $path")
        virtualFileMapper.updateSize(vf.fid, vf.size + ds)
        val p = path.parent
        if (p same path)
            return
        updateParentSize(p, ds)
    }

    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRES_NEW)
    fun insertFile(user: UserInfo, path: VirtualPath, hash: Hash, size: Long, dataFile: File) {
        if (size != dataFile.length())
            throw IllegalArgumentException("文件大小不匹配: $path")
        val time = System.currentTimeMillis().timestamp
        val parent = path.parent
        virtualFileMapper.insert(
            VirtualFile(
                fid = path.id,
                name = path.name,
                parent = parent.id,
                hash = hash,
                owner = user.uid,
                createTime = time,
                updateTime = time,
                size = size
            )
        )
        updateParentSize(parent, size)

        if (fileMappingMapper.selectByHash(hash) == null) {
            val ins = dataFile.inputStream().buffered()
            val type = FileDetector.detect(ins, path.name)
            val fileHash = Hash(ins.use { it.sha256 })
            if (hash != fileHash)
                throw IllegalStateException("文件hash不匹配: $fileHash!=$hash")
            fileMappingMapper.insert(
                FileMapping(
                    hash = hash,
                    dataPath = "data/${hash.pathSafe}",
                    type = type.type,
                    subType = type.subtype,
                    preview = previewGeneratorFactory.canPreview(type),
                    size = size,
                )
            )
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun upload(
        user: UserInfo,
        path: VirtualPath,
        hash: Hash,
        fileSize: Long,
        range: FileRange,
        ins: InputStream
    ): Boolean {
        if (exists(path))
            throw ErrorCode.FILE_EXISTS.data(path.path)
        val dataFile = dataFile(hash)
        if (dataFile.exists()) {
            insertFile(user, path, hash, fileSize, dataFile)
            return true
        }

        val tmpFile = tmpFile(user, hash)
        tmpFile.mkParent()

        if (range.start == fileSize) {
            if (!tmpFile.exists())
                throw IllegalArgumentException("文件不存在: $path")
            insertFile(user, path, hash, fileSize, tmpFile)
            tmpFile.toPath().fileSystem
            dataFile.mkParent()
            Files.move(tmpFile.toPath(), dataFile.toPath())
            return true
        }

        val rf = RandomAccessFile(tmpFile, "rw")
        rf.seek(range.start)

        val buf = ByteArray(1024 * 1024)
        var read = 0L
        while (true) {
            val len = ins.read(buf)
            if (len <= 0)
                break
            rf.write(buf, 0, len)
            read += len
            if (read >= range.size)
                break
        }

        if (rf.filePointer >= fileSize)
            rf.setLength(fileSize)

        rf.close()
        return false
    }

    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRES_NEW)
    protected fun insertDir(owner: Uid, dir: VirtualPath) {
        if (dir.isRoot) {
            val time = System.currentTimeMillis().timestamp
            virtualFileMapper.insert(
                VirtualFile(
                    fid = dir.id,
                    name = "",
                    parent = Hash(IVirtualFile.ID_LENGTH),
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

    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRES_NEW)
    override fun delete(path: VirtualPath) {
        val vf = virtualFileMapper.selectById(path.id)
            ?: throw NoSuchFileException(path.fullPath)
        val hash = vf.hash
        if (hash == null) {
            if (virtualFileMapper.hasChildren(vf.fid))
                throw DirectoryNotEmptyException(path.fullPath)
            virtualFileMapper.deleteById(vf.fid)
            return
        }
        val count = virtualFileMapper.countHash(hash)
        virtualFileMapper.deleteById(vf.fid)
        updateParentSize(path.parent, -vf.size)
        if (count == 1) {
            val fm = fileMappingMapper.selectByHash(hash)
                ?: throw IllegalStateException("hash=$hash not found in mapping")
            fileMappingMapper.deleteById(hash)
            FileMappingService.dataFile(fm.dataPath).del()
            if (fm.preview)
                FileMappingService.previewFile(fm.dataPath).del()
        }
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
