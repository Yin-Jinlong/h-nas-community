package com.yjl.hnas.utils

import java.io.File

/**
 * @author YJL
 */
object FileUtils {

    val DIR: File = File(".").absoluteFile
    val DATA_DIR: File = File("data").absoluteFile
    val CACHE_DIR: File = File("cache").absoluteFile

    const val TYPE_CACHE_THUMBNAIL = "缩略图"
    const val TYPE_IMAGE = "image"

    fun get(vararg subs: String) = DIR.resolve(subs.joinToString("/"))

    fun getData(vararg types: String) = File(DATA_DIR, types.joinToString("/"))

    fun getCache(vararg subs: String) = File(CACHE_DIR, subs.joinToString("/"))
}
