package com.yjl.hnas.utils

import java.net.URLDecoder

val String.deUrl: String
    get() = URLDecoder.decode(this, "UTF-8")
