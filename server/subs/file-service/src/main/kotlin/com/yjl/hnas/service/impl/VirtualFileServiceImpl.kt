package com.yjl.hnas.service.impl

import com.yjl.hnas.audio.AudioInfoHelper
import com.yjl.hnas.data.AudioFileInfo
import com.yjl.hnas.data.DataHelper
import com.yjl.hnas.data.FileRange
import com.yjl.hnas.entity.*
import com.yjl.hnas.error.ErrorCode
import com.yjl.hnas.fs.*
import com.yjl.hnas.fs.attr.FileAttributes
import com.yjl.hnas.mapper.AudioInfoMapper
import com.yjl.hnas.mapper.ChildrenCountMapper
import com.yjl.hnas.mapper.FileMappingMapper
import com.yjl.hnas.mapper.VirtualFileMapper
import com.yjl.hnas.service.VirtualFileService
import com.yjl.hnas.tika.FileDetector
import com.yjl.hnas.utils.del
import com.yjl.hnas.utils.mkParent
import com.yjl.hnas.utils.timestamp
import io.github.yinjinlong.md.sha256
import org.apache.tika.mime.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.io.BufferedInputStream
import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.SeekableByteChannel
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import java.sql.Timestamp
import kotlin.io.path.name

/**
 * @author YJL
 */
@Service
class VirtualFileServiceImpl(
    val virtualFileMapper: VirtualFileMapper,
    val fileMappingMapper: FileMappingMapper,
    val childrenCountMapper: ChildrenCountMapper,
    val audioInfoMapper: AudioInfoMapper,
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
        val vf = getOrThrow(path)
        if (vf.hash == null)
            throw IllegalArgumentException("非文件: $path")
        val fm = fileMappingMapper.selectByHash(vf.hash!!)
            ?: throw IllegalStateException("文件映射不存在: $path")
        return Files.newByteChannel(DataHelper.dataFile(fm.dataPath).toPath())
    }

    override fun genId(path: VirtualPath): Hash {
        return Hash(path.toAbsolutePath().fullPath.sha256)
    }

    override fun get(path: VirtualPath): IVirtualFile? {
        return virtualFileMapper.selectById(path.id)
    }

    fun getOrThrow(path: VirtualPath): IVirtualFile {
        return virtualFileMapper.selectById(path.id)
            ?: throw NoSuchFileException(path.fullPath)
    }

    override fun getByParent(parent: VirtualPath, type: String?): List<IVirtualFile> {
        val p = parent.toAbsolutePath()
        if (!exists(p) && !p.isRoot)
            throw NoSuchFileException(p.fullPath)
        return virtualFileMapper.selectsByParent(p.id)
    }

    fun tmpFile(user: Uid, hash: Hash): File {
        return DataHelper.dataSub("tmp/$user/$hash.tmp")
    }

    fun dataPath(type: MediaType, hash: Hash) = "$type/$hash"

    fun dataFile(type: MediaType, hash: Hash): File {
        return DataHelper.dataFile(dataPath(type, hash))
    }

    private tailrec fun updateParent(path: VirtualPath, op: VirtualFile.() -> Unit) {
        val vf = virtualFileMapper.selectByIdLock(path.id)
            ?: throw IllegalStateException("数据库文件不存在: $path")
        vf.op()
        val p = path.parent
        if (p same path)
            return
        updateParent(p, op)
    }

    private fun updateParentSize(path: VirtualPath, ds: Long) = updateParent(path) {
        virtualFileMapper.updateSize(fid, size + ds)
    }

    private fun updateParentUpdateTime(path: VirtualPath, time: Timestamp) = updateParent(path) {
        virtualFileMapper.updateUpdateTime(fid, time)
    }

    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRES_NEW)
    fun insertFile(
        owner: Uid,
        user: Uid,
        path: VirtualPath,
        hash: Hash,
        size: Long,
        mediaType: MediaType,
        dataFile: File,
        dataPath: String
    ) {
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
                owner = owner,
                user = user,
                mediaType = mediaType.toString(),
                createTime = time,
                updateTime = time,
                size = size
            )
        )
        updateParentSize(parent, size)
        updateParentUpdateTime(parent, time)
        updateCount(parent, 1)

        if (fileMappingMapper.selectByHash(hash) == null) {
            val ins = dataFile.inputStream().buffered()
            val type = FileDetector.detect(ins, path.name)
            val fileHash = Hash(ins.use { it.sha256 })
            if (hash != fileHash)
                throw IllegalStateException("文件hash不匹配: $fileHash!=$hash")
            fileMappingMapper.insert(
                FileMapping(
                    hash = hash,
                    dataPath = dataPath,
                    type = type.type,
                    subType = type.subtype,
                    preview = true,
                    size = size,
                )
            )
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun upload(
        owner: Uid,
        path: VirtualPath,
        hash: Hash,
        fileSize: Long,
        range: FileRange,
        ins: BufferedInputStream
    ): Boolean {
        if (exists(path))
            throw ErrorCode.FILE_EXISTS.data(path.path)

        val user = if (path.isPublic()) 0 else owner

        if (range.start == 0L) {
            val type = FileDetector.detect(ins, path.name)
            val dataFile = dataFile(type, hash)
            if (dataFile.exists()) {
                insertFile(owner, user, path, hash, fileSize, type, dataFile, dataPath(type, hash))
                return true
            }
        }

        val tmpFile = tmpFile(owner, hash)
        tmpFile.mkParent()

        if (range.start == fileSize) {
            if (fileSize == 0L) {
                tmpFile.createNewFile()
            } else if (!tmpFile.exists())
                throw IllegalArgumentException("文件不存在: $path")
            val type = tmpFile.inputStream().buffered().use { FileDetector.detect(it, path.name) }
            val dataFile = dataFile(type, hash)
            insertFile(owner, user, path, hash, fileSize, type, tmpFile, dataPath(type, hash))
            dataFile.mkParent()
            Files.move(tmpFile.toPath(), dataFile.toPath())
            return true
        }

        RandomAccessFile(tmpFile, "rw").use { rf ->
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
        }

        return false
    }

    private tailrec fun updateCount(path: VirtualPath, dSubCount: Int, dSubsCount: Int = dSubCount) {
        val cc = childrenCountMapper.selectByFidLock(path.id)
            ?: throw IllegalStateException("children_count 不存在目录：$path id: ${path.id}")
        childrenCountMapper.updateCount(cc.fid, cc.subCount + dSubCount, cc.subsCount + dSubsCount)
        if (path.isRoot)
            return
        val parent = path.parent
        updateCount(parent, 0, dSubsCount)
    }

    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRES_NEW)
    protected fun insertDir(owner: Uid, user: Uid, dir: VirtualPath) {
        val fid = dir.id
        if (dir.isRoot) {
            val time = System.currentTimeMillis().timestamp
            virtualFileMapper.insert(
                VirtualFile(
                    fid = fid,
                    name = "",
                    parent = Hash(IVirtualFile.ID_LENGTH),
                    hash = null,
                    createTime = time,
                    updateTime = time,
                )
            )
            childrenCountMapper.insert(fid)
            return
        }

        val p = dir.parent
        if (!exists(p))
            insertDir(owner, user, p)

        if (p same dir)
            return
        val time = System.currentTimeMillis().timestamp
        virtualFileMapper.insert(
            VirtualFile(
                fid = fid,
                name = dir.name,
                parent = p.id,
                hash = null,
                owner = owner,
                user = user,
                createTime = time,
                updateTime = time,
            )
        )
        childrenCountMapper.insert(fid)
        updateParentUpdateTime(p, time)
        updateCount(p, 1)
    }

    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRES_NEW)
    override fun rename(path: VirtualPath, name: String) {
        val oldId = path.id
        val vf = virtualFileMapper.selectById(oldId)
            ?: throw NoSuchFileException(path.fullPath)
        val new = path.parent.resolve(name)
        val newId = new.id
        val time = System.currentTimeMillis().timestamp
        virtualFileMapper.insert(
            vf.copy(
                fid = newId,
                name = name,
                updateTime = time,
            )
        )
        virtualFileMapper.deleteById(vf.fid)
        val cc = childrenCountMapper.selectByFidLock(oldId)
            ?: throw IllegalStateException("children_count 不存在目录：$oldId")
        childrenCountMapper.updateId(cc.fid, newId)
    }

    override fun getFolderChildrenCount(path: VirtualPath): ChildrenCount {
        return childrenCountMapper.selectByFid(path.id)
            ?: throw NotDirectoryException(path.fullPath)
    }

    fun checkAudio(vf: IVirtualFile): AudioInfo? {
        if (!vf.mediaType.startsWith("audio"))
            throw ErrorCode.BAD_FILE_FORMAT.error
        return audioInfoMapper.selectByHash(vf.hash ?: throw ErrorCode.BAD_FILE_FORMAT.error)
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun getAudioInfo(path: VirtualPath): AudioFileInfo {
        val vf = getOrThrow(path)
        val ai = checkAudio(vf)
        if (ai != null)
            return AudioFileInfo.of(path.path, ai)
        val fm = fileMappingMapper.selectByHash(vf.hash!!)
            ?: throw IllegalStateException("file_mapping 不存在hash: ${vf.hash}")
        return AudioFileInfo.of(
            path.path,
            (AudioInfoHelper.getInfo(vf.hash!!, fm) ?: AudioInfo(fid = path.id))
                .apply(audioInfoMapper::insert)
        )
    }

    override fun getAudioCover(path: VirtualPath): File {
        val vf = getOrThrow(path)
        val ai = checkAudio(vf)
            ?: throw ErrorCode.BAD_FILE_FORMAT.error
        if (!ai.cover.isNullOrEmpty()) {
            return DataHelper.coverFile(ai.cover!!).apply {
                if (!exists()) {
                    val fm = fileMappingMapper.selectByHash(ai.fid)
                        ?: throw IllegalStateException("file_mapping 不存在hash: ${ai.fid}")
                    AudioInfoHelper.saveCover(DataHelper.dataFile(fm.dataPath))
                }
            }
        }
        throw ErrorCode.NO_SUCH_FILE.error
    }

    @Transactional(rollbackFor = [Exception::class])
    fun mkdirs(owner: Uid, dir: VirtualPath) {
        if (exists(dir))
            throw FileAlreadyExistsException(dir.fullPath)
        insertDir(owner, if (dir.isPublic()) 0 else owner, dir)
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
        val parent = path.parent
        val vf = getOrThrow(path)
        val time = System.currentTimeMillis().timestamp
        val hash = vf.hash
        if (hash == null) {
            if (virtualFileMapper.hasChildren(vf.fid))
                throw DirectoryNotEmptyException(path.fullPath)
            virtualFileMapper.deleteById(vf.fid)
            childrenCountMapper.deleteById(vf.fid)
            updateCount(parent, -1)
            updateParentUpdateTime(parent, time)
            return
        }
        val count = virtualFileMapper.countHash(hash)
        virtualFileMapper.deleteById(vf.fid)
        updateParentSize(parent, -vf.size)
        updateParentUpdateTime(parent, time)
        updateCount(parent, -1)
        if (count == 1) {
            val fm = fileMappingMapper.selectByHash(hash)
                ?: throw IllegalStateException("hash=$hash not found in mapping")
            fileMappingMapper.deleteById(hash)
            if (fm.type == "audio") {
                audioInfoMapper.deleteById(hash)
            }
            DataHelper.dataFile(fm.dataPath).del()
            if (fm.preview)
                DataHelper.previewFile(fm.dataPath).del()
            if (fm.type == "video") {
                DataHelper.hlsIndexFile(hash.pathSafe).apply {
                    while (exists())
                        delete()
                }
                File(DataHelper.hlsPath(fm.hash.pathSafe)).apply {
                    while (exists())
                        deleteRecursively()
                }
            }
        }
    }

    override fun copy(source: VirtualPath, target: VirtualPath, options: Set<CopyOption>) {
        TODO("Not yet implemented")
    }

    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRES_NEW)
    override fun move(source: VirtualPath, target: VirtualPath, options: Set<CopyOption>) {
        copy(source, target, options)
        delete(source)
    }

    override fun isSameFile(path: VirtualPath, path2: VirtualPath): Boolean {
        val f1 = get(path) ?: return false
        val f2 = get(path2) ?: return false
        return if (f1.isFolder() && f2.isFolder())
            f1.fid == f2.fid
        else f1.hash == f2.hash
    }

    override fun isHidden(path: VirtualPath): Boolean {
        TODO("Not yet implemented")
    }

    override fun getFileStore(path: VirtualPath): FileStore {
        return VirtualFileStore(getOrThrow(path.root))
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

    @Suppress("UNCHECKED_CAST")
    override fun <A : BasicFileAttributes> readAttributes(
        path: VirtualPath,
        type: Class<A>,
        options: Set<LinkOption>
    ): A {
        if (type != BasicFileAttributes::class.java && type != VirtualFileAttributes::class.java)
            throw UnsupportedOperationException("unsupported type $type")
        return VirtualFileAttributes(getOrThrow(path)) as A
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
