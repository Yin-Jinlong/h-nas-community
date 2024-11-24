package com.yjl.hnas.fs.attr


/**
 * @author YJL
 */
open class FileAttribute<T>(
    val name: String,
    val value: T
) : java.nio.file.attribute.FileAttribute<T> {

    override fun name() = name

    override fun value() = value
}

object FileAttributes {
    const val OWNER = "owner"
    const val HASH = "hash"
    const val TYPE = "type"
}
