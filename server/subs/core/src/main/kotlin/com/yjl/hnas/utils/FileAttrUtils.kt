package com.yjl.hnas.utils

import com.yjl.hnas.fs.attr.FileAttribute
import org.apache.tika.mime.MediaType

fun FileOwnerAttribute(uid: Long) = FileAttribute(FileAttribute.OWNER, uid)

fun FileHashAttribute(hash: String) = FileAttribute(FileAttribute.HASH, hash)

fun FileTypeAttribute(type: MediaType) = FileAttribute(FileAttribute.TYPE, type)
