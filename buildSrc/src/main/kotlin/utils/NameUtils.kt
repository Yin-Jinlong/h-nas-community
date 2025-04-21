package utils

import java.util.*

val String.dartString: String
    get() = "'$this'"

val String.dartClassName: String
    get() = this.replace(".", "$")

val String.underline2smallCamel: String
    get() {
        // 下划线
        val parts = this.split("_")
        return if (parts.size == 1) {
            parts[0].lowercase(Locale.getDefault())
        } else {
            parts.joinToString("") {
                it.lowercase(Locale.getDefault()).replaceFirstChar { c ->
                    if (c.isLowerCase()) c.titlecase(Locale.getDefault()) else c.toString()
                }
            }
        }
    }
