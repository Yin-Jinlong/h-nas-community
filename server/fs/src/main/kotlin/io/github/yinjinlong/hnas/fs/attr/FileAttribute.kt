package io.github.yinjinlong.hnas.fs.attr

import java.nio.file.attribute.FileAttribute


/**
 * @author YJL
 */
open class FileAttribute<T>(
    val name: String,
    val value: T
) : FileAttribute<T> {

    override fun name() = name

    override fun value() = value
}

object FileAttributes {
    const val OWNER = "owner"
    const val ROLE = "role"
}
