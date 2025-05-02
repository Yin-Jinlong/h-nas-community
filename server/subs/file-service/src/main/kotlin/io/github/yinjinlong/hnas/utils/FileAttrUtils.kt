package io.github.yinjinlong.hnas.utils

import io.github.yinjinlong.hnas.fs.attr.FileAttribute
import io.github.yinjinlong.hnas.fs.attr.FileAttributes
import org.apache.tika.mime.MediaType

fun FileOwnerAttribute(uid: Long) = FileAttribute(FileAttributes.OWNER, uid)
