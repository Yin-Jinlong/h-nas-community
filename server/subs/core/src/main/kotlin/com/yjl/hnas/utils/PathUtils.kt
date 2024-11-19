package com.yjl.hnas.utils

import java.io.File

fun File.del() {
    if (!delete())
        deleteOnExit()
}
