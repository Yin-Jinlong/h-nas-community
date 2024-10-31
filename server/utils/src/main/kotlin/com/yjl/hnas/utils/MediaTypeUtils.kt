package com.yjl.hnas.utils

import org.apache.tika.Tika
import com.yjl.hnas.tika.FileDetector
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Path

val tika = Tika(FileDetector)

val File.mimeType: String
    get() = tika.detect(this)

val InputStream.mimeType: String
    get() = tika.detect(this)

val Path.mimeType: String
    get() = tika.detect(this)

val URL.mimeType: String
    get() = tika.detect(this)

val ByteArray.mimeType: String
    get() = tika.detect(this)
