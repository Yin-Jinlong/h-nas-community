package io.github.yinjinlong.hnas.fs

import java.nio.file.*
import java.nio.file.attribute.UserPrincipalLookupService

/**
 * @author YJL
 */
class VirtualFilesystem(
    val fsp: VirtualFileSystemProvider
) : FileSystem() {

    companion object {
        const val SEPARATOR = "/"
        const val SEPARATOR2 = "//"

        private fun String.replaceSeparator() = replace("\\", SEPARATOR)

        fun parsePaths(str: StringBuilder): List<String> {
            if (str.isEmpty())
                return listOf()
            if (str.startsWith(SEPARATOR))
                str.delete(0, 1)
            val rootIndex = str.indexOf(SEPARATOR2)
            return (if (rootIndex == -1) str
            else str.substring(rootIndex + 2))
                .split(SEPARATOR)
        }
    }

    override fun close() = Unit

    override fun provider() = fsp

    override fun isOpen() = true

    override fun isReadOnly() = false

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

    @Throws(InvalidPathException::class)
    fun getPubPath(vararg paths: String) = getPath("", paths)

    @Throws(InvalidPathException::class)
    fun getUserPath(uid: Long, vararg paths: String) = getPath("$uid:", paths)

    @Throws(InvalidPathException::class)
    fun getPath(first: String) = getPath(first, arrayOf())

    override fun getPath(first: String, more: Array<out String>): VirtualPath {
        val sb = StringBuilder()
        val access: Long?
        val abs: Boolean
        if (first.isEmpty()) {
            access = null
            abs = more.isNotEmpty() && more[0].startsWith(SEPARATOR)
        } else {
            val accessStr = first.substringBefore(":", "")
            val firstPath = first.substringAfter(":")
            access = accessStr.toLongOrNull()
            if (firstPath.isNotEmpty()) {
                abs = firstPath.startsWith(SEPARATOR)
                sb.append(firstPath)
                if (more.isNotEmpty())
                    sb.append(SEPARATOR)
            } else {
                abs = more.isNotEmpty() && more[0].startsWith(SEPARATOR)
            }
        }
        more.joinTo(sb, SEPARATOR) { part ->
            part.replaceSeparator().also {
                if (it.isEmpty())
                    throw InvalidPathException(more.joinToString(SEPARATOR), "Empty path name")
                if (VirtualPath.BAD_PATH_REGEX.containsMatchIn(it))
                    throw InvalidPathException(more.joinToString(SEPARATOR), "Invalid path name: $it")
            }
        }
        if (sb.startsWith(SEPARATOR))
            sb.delete(0, 1)
        if (sb.endsWith(SEPARATOR))
            sb.delete(sb.length - 1, sb.length)
        return VirtualPath(this, access, abs, parsePaths(sb))
    }

    override fun getPathMatcher(syntaxAndPattern: String): PathMatcher {
        TODO("Not yet implemented")
    }

    override fun getUserPrincipalLookupService(): UserPrincipalLookupService {
        TODO("Not yet implemented")
    }

    override fun newWatchService(): WatchService {
        TODO("Not yet implemented")
    }
}
