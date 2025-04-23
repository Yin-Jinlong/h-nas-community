package io.github.yinjinlong.hnas.utils

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
private val Base64Url = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)

@OptIn(ExperimentalEncodingApi::class)
val ByteArray.base64Url: String
    get() = Base64Url.encode(this)

@OptIn(ExperimentalEncodingApi::class)
val String.unBase64Url: String
    get() = Base64Url.decode(this).decodeToString()

@OptIn(ExperimentalEncodingApi::class)
val String.unBase64UrlBytes: ByteArray
    get() = Base64Url.decode(this)

@OptIn(ExperimentalEncodingApi::class)
val String.reBase64Url: String
    get() = Base64Url.encode(Base64Url.decode(this))
