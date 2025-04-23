package io.github.yinjinlong.hnas.utils

import io.github.yinjinlong.hnas.tika.FileDetector
import java.io.File

fun File.mimeType(name: String) = inputStream().buffered().use {
    FileDetector.detect(it, name)
}
