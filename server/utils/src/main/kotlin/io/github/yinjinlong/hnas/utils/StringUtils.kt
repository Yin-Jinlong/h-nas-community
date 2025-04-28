package io.github.yinjinlong.hnas.utils

/**
 * @author YJL
 */
val String.came2under: String
    get() = StringBuilder().let {r->
        forEach {
            if (it.isUpperCase()) {
                if (r.isNotEmpty())
                    r.append("_")
                r.append(it.lowercaseChar())
            } else {
                r.append(it)
            }
        }
        r.toString()
    }
