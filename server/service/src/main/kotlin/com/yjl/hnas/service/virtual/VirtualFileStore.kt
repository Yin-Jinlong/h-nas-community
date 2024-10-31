package com.yjl.hnas.service.virtual

import com.yjl.hnas.entity.Uid
import java.nio.file.FileStore
import java.nio.file.attribute.FileAttributeView
import java.nio.file.attribute.FileStoreAttributeView

/**
 * @author YJL
 */
class VirtualFileStore internal constructor(
    val user: Uid?
) : FileStore() {
    override fun name() = (user ?: "public").toString()

    override fun type() = "virtual"

    override fun isReadOnly() = false

    override fun getTotalSpace() = Long.MAX_VALUE

    override fun getUsableSpace(): Long {
        TODO("Not yet implemented")
    }

    override fun getUnallocatedSpace(): Long {
        TODO("Not yet implemented")
    }

    override fun supportsFileAttributeView(type: Class<out FileAttributeView>?) = false

    override fun supportsFileAttributeView(name: String?) = false

    override fun <V : FileStoreAttributeView?> getFileStoreAttributeView(type: Class<V>?): V {
        TODO("Not yet implemented")
    }

    override fun getAttribute(attribute: String?): Any {
        TODO("Not yet implemented")
    }
}