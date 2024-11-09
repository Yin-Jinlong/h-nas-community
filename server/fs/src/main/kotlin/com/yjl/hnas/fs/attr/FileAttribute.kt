package com.yjl.hnas.fs.attr

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
