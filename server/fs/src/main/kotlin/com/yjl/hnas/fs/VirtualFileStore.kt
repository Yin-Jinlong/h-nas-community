package com.yjl.hnas.fs

import com.yjl.hnas.entity.IVirtualFile
import java.nio.file.FileStore
import java.nio.file.attribute.FileAttributeView
import java.nio.file.attribute.FileStoreAttributeView

/**
 * @author YJL
 */
class VirtualFileStore(
    private val rootFile: IVirtualFile
) : FileStore() {

    private val name = rootFile.user.toString()

    override fun name() = name

    override fun type() = "virtual"

    override fun isReadOnly() = false

    override fun getTotalSpace() = Long.MAX_VALUE

    override fun getUsableSpace() = rootFile.size

    override fun getUnallocatedSpace() = Long.MAX_VALUE

    override fun supportsFileAttributeView(type: Class<out FileAttributeView>?): Boolean {
        return false
    }

    override fun supportsFileAttributeView(name: String?): Boolean {
        return false
    }

    override fun <V : FileStoreAttributeView> getFileStoreAttributeView(type: Class<V>?): V? {
        return null
    }

    override fun getAttribute(attribute: String?): Any? {
        return null
    }
}
