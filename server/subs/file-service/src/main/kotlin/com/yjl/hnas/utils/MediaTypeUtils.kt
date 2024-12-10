package com.yjl.hnas.utils

val String.isAudioMediaType: Boolean
    get() = lowercase().let { it == "audio" || it.startsWith("audio/") }

val String.isVideoMediaType: Boolean
    get() = lowercase().let { it == "video" || it.startsWith("video/") }
