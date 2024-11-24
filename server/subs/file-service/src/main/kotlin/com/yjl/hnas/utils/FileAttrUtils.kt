package com.yjl.hnas.utils

import com.yjl.hnas.fs.attr.FileAttribute
import com.yjl.hnas.fs.attr.FileAttributes
import org.apache.tika.mime.MediaType

fun FileOwnerAttribute(uid: Long) = FileAttribute(FileAttributes.OWNER, uid)

fun FileHashAttribute(hash: String) = FileAttribute(FileAttributes.HASH, hash)

fun FileTypeAttribute(type: MediaType) = FileAttribute(FileAttributes.TYPE, type)
