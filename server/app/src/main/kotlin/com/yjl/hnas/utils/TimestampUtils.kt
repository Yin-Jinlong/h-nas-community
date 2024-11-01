package com.yjl.hnas.utils

import java.sql.Timestamp

val Long.timestamp: Timestamp
    get() = Timestamp(this)
