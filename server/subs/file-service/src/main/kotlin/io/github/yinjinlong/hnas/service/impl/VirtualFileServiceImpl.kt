package io.github.yinjinlong.hnas.service.impl

import com.google.gson.Gson
import com.google.gson.JsonElement
import io.github.yinjinlong.hnas.audio.AudioInfoHelper
import io.github.yinjinlong.hnas.data.AudioFileInfo
import io.github.yinjinlong.hnas.data.DataHelper
import io.github.yinjinlong.hnas.data.FileRange
import io.github.yinjinlong.hnas.entity.*
import io.github.yinjinlong.hnas.error.ErrorCode
import io.github.yinjinlong.hnas.fe.FileExtraReaderHelper
import io.github.yinjinlong.hnas.fs.VirtualFileAttributes
import io.github.yinjinlong.hnas.fs.VirtualFileStore
import io.github.yinjinlong.hnas.fs.VirtualPath
import io.github.yinjinlong.hnas.fs.attr.FileAttributes
import io.github.yinjinlong.hnas.mapper.ChildrenCountMapper
import io.github.yinjinlong.hnas.mapper.FileMappingMapper
import io.github.yinjinlong.hnas.mapper.VirtualFileMapper
import io.github.yinjinlong.hnas.service.AbstractVirtualFileService
import io.github.yinjinlong.hnas.service.TooManyChildrenException
import io.github.yinjinlong.hnas.tika.FileDetector
import io.github.yinjinlong.hnas.utils.del
import io.github.yinjinlong.hnas.utils.isVideoMediaType
import io.github.yinjinlong.hnas.utils.mkParent
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
    virtualFileMapper: VirtualFileMapper,
    val fileMappingMapper: FileMappingMapper,
    childrenCountMapper: ChildrenCountMapper,
    val gson: Gson,
    val fileExtraReaderHelper: FileExtraReaderHelper,
) : AbstractVirtualFileService(virtualFileMapper, childrenCountMapper) {

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

    fun tmpFile(user: Uid, hash: Hash): File {
        return DataHelper.dataSub("tmp/$user/$hash.tmp")
    }

    fun dataPath(type: MediaType, hash: Hash) = "$type/$hash"

    fun dataFile(type: MediaType, hash: Hash): File {
        return DataHelper.dataFile(dataPath(type, hash))
    }

    fun getFileMapping(hash: Hash): IFileMapping {
        return fileMappingMapper.selectByHash(hash)
            ?: throw IllegalStateException("file_mapping不存在: $hash")
    }

    private tailrec fun updateParent(path: VirtualPath, op: VirtualFile.() -> Unit) {
        val vf = virtualFileMapper.selectByIdLock(pathIdOrThrow(path))
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
        dataFile: File,
        dataPath: String,
    ) {
        if (size != dataFile.length())
            throw IllegalArgumentException("文件大小不匹配: $path")
        val parent = path.parent
        virtualFileMapper.insert(
            VirtualFile(
                name = path.name,
                parent = parent.id,
                hash = hash,
                owner = owner,
                user = user,
                size = size
            )
        )
        updateParentSize(parent, size)

        val time = virtualFileMapper.selectUpdateTimeById(path.id)
            ?: throw IllegalStateException("数据库文件不存在: $path")

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
                insertFile(owner, user, path, hash, fileSize, dataFile, dataPath(type, hash))
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
            val type = tmpFile.inputStream().buffered().use {
                FileDetector.detect(it, path.name)
            }
            val dataFile = dataFile(type, hash)
            insertFile(owner, user, path, hash, fileSize, tmpFile, dataPath(type, hash))
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
        if (dir.isRoot) return

        val p = dir.parent
        if (!exists(p))
            insertDir(owner, user, p)

        if (p same dir)
            return
        val id = insertAndGetId(
            VirtualFile(
                name = dir.name,
                parent = p.id,
                hash = null,
                owner = owner,
                user = user,
            )
        )
        childrenCountMapper.insert(id)
        updateParentUpdateTime(
            p, virtualFileMapper.selectUpdateTimeById(id)
                ?: throw IllegalStateException("数据库没有文件: ${dir.fullPath}")
        )
        updateCount(p, 1)
    }

    override fun getUserStorageUsage(user: Uid?): Long {
        return if (user == null) virtualFileMapper.countUserStorageUsage()
        else getRootId(user).let {
            virtualFileMapper.selectById(it)?.size
        } ?: throw ErrorCode.NO_SUCH_USER.data(user)
    }

    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRES_NEW)
    override fun rename(path: VirtualPath, name: String) {
        val owner = path.getOwner()
        val role = path.getRole()
        val vf = getOrThrow(path)
        if (role != IUser.ROLE_ADMIN && !isOwn(vf, owner))
            throw ErrorCode.NO_PERMISSION.data(path.path)
        virtualFileMapper.updateName(vf.fid, name)
        updateParentUpdateTime(
            path.parent,
            virtualFileMapper.selectUpdateTimeById(vf.fid) ?: throw IllegalStateException()
        )
    }

    override fun search(user: Uid, name: String, lastPath: VirtualPath?): List<Pair<VirtualFile, VirtualPath>> {
        val lastID = lastPath?.let { virtualFileMapper.selectById(lastPath.id)?.fid } ?: Hash(IVirtualFile.ID_LENGTH)
        return virtualFileMapper.selectListByName(user, name, lastID, 10).map {
            it to it.path
        }
    }

    override fun getExtra(file: VirtualFile): JsonElement? {
        if (file.hash == null) return null
        val fm = getFileMapping(file.hash!!)
        return fileExtraReaderHelper.getExtra(DataHelper.dataFile(fm.dataPath), MediaType(fm.type, fm.subType)).also {
            virtualFileMapper.updateExtra(file.fid, gson.toJson(it))
        }
    }

    override fun getAudioCover(path: VirtualPath): File {
        val vf = getOrThrow(path)
        if (vf.hash == null) throw ErrorCode.BAD_REQUEST.error
        val ai = gson.fromJson(vf.extra, AudioFileInfo::class.java)
        if (!ai.cover.isNullOrEmpty()) {
            return DataHelper.coverFile(ai.cover!!).apply {
                if (!exists()) {
                    val fm = getFileMapping(vf.hash!!)
                    AudioInfoHelper.saveCover(DataHelper.dataFile(fm.dataPath))
                }
            }
        }
        throw ErrorCode.NO_SUCH_FILE.error
    }

    override fun getAudioLrc(path: VirtualPath): ByteArray {
        val vf = getOrThrow(path)
        val extra = vf.extra ?: return ByteArray(0)
        if (extra.isJsonObject) {
            val lrc = extra.asJsonObject.get("lrc")
            if (lrc.isJsonPrimitive && lrc.asBoolean) {
                val file = DataHelper.lrcFile(vf.hash!!.pathSafe)
                return if (!file.exists()) {
                    val fm = getFileMapping(vf.hash!!)
                    AudioInfoHelper.getLrc(DataHelper.dataFile(fm.dataPath), MediaType(fm.type, fm.subType))
                        ?.also {
                            file.mkParent()
                            file.writeText(it)
                        }?.encodeToByteArray() ?: ByteArray(0)
                } else {
                    file.readBytes()
                }
            }
        }
        return ByteArray(0)
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
        val vf = getOrThrow(dir)
        if (!vf.isFolder())
            throw NotDirectoryException(dir.fullPath)
        return object : DirectoryStream<VirtualPath> {
            override fun close() {

            }

            override fun iterator(): MutableIterator<VirtualPath> {
                val files = getByParent(dir, null).map {
                    dir.resolve(it.name)
                }
                return files.toMutableList().listIterator()
            }

        }
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun createDirectory(dir: VirtualPath, attrs: Map<String, FileAttribute<*>>) {
        val owner = attrs[FileAttributes.OWNER]
            ?: throw IllegalArgumentException("owner is null: $dir")
        mkdirs(owner.value() as Uid, dir.toAbsolutePath())
    }

    fun isOwn(file: IVirtualFile, user: Uid): Boolean {
        var vf = file
        while (!vf.parent.isZero) {
            if (vf.owner == user)
                return true
            vf = virtualFileMapper.selectById(vf.parent) ?: break
        }
        return false
    }

    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRES_NEW)
    override fun delete(path: VirtualPath) {
        val owner = path.getOwner()
        val role = path.getRole()
        val vf = getOrThrow(path)
        if (role != IUser.ROLE_ADMIN && !isOwn(vf, owner))
            throw AccessDeniedException(path.path)
        val parent = path.parent
        val hash = vf.hash
        if (hash == null) {
            val counts = childrenCountMapper.selectByFid(vf.fid)
                ?: throw IllegalStateException("children_count 不存在目录：${path.fullPath}")
            if (counts.subsCount > 100)
                throw TooManyChildrenException(path.fullPath)
            else if (counts.subsCount > 0) {
                val children = getByParent(path, null)
                for (child in children) {
                    delete(path.resolve(child.name))
                }
            }
            val time = Timestamp(System.currentTimeMillis())
            virtualFileMapper.deleteById(vf.fid)
            childrenCountMapper.deleteById(vf.fid)
            updateCount(parent, -1)
            updateParentUpdateTime(parent, time)
            return
        }
        val count = virtualFileMapper.countHash(hash)
        val time = Timestamp(System.currentTimeMillis())
        virtualFileMapper.deleteById(vf.fid)
        updateParentSize(parent, -vf.size)
        updateParentUpdateTime(parent, time)
        updateCount(parent, -1)
        if (count == 1) {
            val fm = fileMappingMapper.selectByHash(hash)
                ?: throw IllegalStateException("hash=$hash not found in mapping")
            fileMappingMapper.deleteById(hash)
            DataHelper.dataFile(fm.dataPath).del()
            if (fm.preview)
                DataHelper.previewFile(fm.dataPath).del()
            if (fm.type.isVideoMediaType) {
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

    private fun VirtualPath.getOwner(): Uid {
        val owner = bundledAttributes[FileAttributes.OWNER] as Uid?
            ?: throw IllegalArgumentException("owner is null: $path")
        return owner
    }

    private fun VirtualPath.getRole(): String {
        val role = bundledAttributes[FileAttributes.ROLE] as String?
        return role ?: IUser.ROLE_USER
    }
}
