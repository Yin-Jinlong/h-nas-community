package com.yjl.hnas.service.virtual

import com.yjl.hnas.service.VirtualFileService
import java.nio.file.*
import java.nio.file.attribute.UserPrincipalLookupService

/**
 * 虚拟文件系统
 *
 * uri:
 * - `vfile:///<path>` 公开
 * - `vfile://<uid>/<path>` 私有
 *
 * @author YJL
 */
class VirtualFileSystem internal constructor(
    private val provider: VirtualFileSystemProvider
) : FileSystem() {

    companion object {
        const val SEPARATOR = "/"

        internal lateinit var virtualFileService: VirtualFileService

        private var inited = false

        fun init(
            virtualFileService: VirtualFileService
        ) {
            if (inited)
                return
            Companion.virtualFileService = virtualFileService

            inited = true
        }

    }

    override fun close() {
        throw UnsupportedOperationException()
    }

    override fun provider() = provider

    override fun isOpen() = true

    override fun isReadOnly(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getSeparator() = SEPARATOR

    override fun getRootDirectories(): MutableIterable<Path> {
        TODO("Not yet implemented")
    }

    override fun getFileStores(): MutableIterable<FileStore> {
        TODO("Not yet implemented")
    }

    override fun supportedFileAttributeViews(): MutableSet<String> {
        TODO("Not yet implemented")
    }

    override fun getPath(user: String, vararg more: String?): VirtualPath {
        return VirtualPath(
            this,
            if (user.isEmpty()) null
            else user.toLongOrNull()
                ?: throw IllegalArgumentException("Invalid user id: $user"),
            more.joinToString(SEPARATOR)
        )
    }

    override fun getPathMatcher(syntaxAndPattern: String?): PathMatcher {
        TODO("Not yet implemented")
    }

    override fun getUserPrincipalLookupService(): UserPrincipalLookupService {
        TODO("Not yet implemented")
    }

    override fun newWatchService(): WatchService {
        TODO("Not yet implemented")
    }
}