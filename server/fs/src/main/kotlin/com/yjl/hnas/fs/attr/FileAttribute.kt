package com.yjl.hnas.fs.attr


/**
 * @author YJL
 */
open class FileAttribute<T>(
    val name: String,
    val value: T
) : java.nio.file.attribute.FileAttribute<T> {

    companion object {
        const val OWNER = "owner"
        const val HASH = "hash"
        const val TYPE = "type"
    }

    override fun name() = name

    override fun value() = value
}
