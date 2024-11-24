package com.yjl.hnas.utils

import java.io.File

fun File.del() {
    if (!delete())
        deleteOnExit()
}

fun File.mkParent() {
    val p = parentFile
    if (!p.exists())
        p.mkdirs()
}
