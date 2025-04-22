package com.yjl.hnas.utils

import com.yjl.hnas.tika.FileDetector
import java.io.File

fun File.mimeType(name: String) = inputStream().buffered().use {
    FileDetector.detect(it, name)
}
