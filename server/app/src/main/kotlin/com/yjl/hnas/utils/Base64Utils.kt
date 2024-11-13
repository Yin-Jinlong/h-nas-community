package com.yjl.hnas.utils

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
val ByteArray.base64: String
    get() = Base64.encode(this)

@OptIn(ExperimentalEncodingApi::class)
val ByteArray.base64Url: String
    get() = Base64.UrlSafe.encode(this)


@OptIn(ExperimentalEncodingApi::class)
val String.unBase64: String
    get() = Base64.decode(this).decodeToString()

@OptIn(ExperimentalEncodingApi::class)
val String.unBase64Url: String
    get() = Base64.UrlSafe.decode(this).decodeToString()
