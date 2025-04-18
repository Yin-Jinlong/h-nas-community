package io.github.yinjinlong.h_nas.utils

import android.content.Context
import android.content.res.Configuration

val Context.isDark: Boolean
    get() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
