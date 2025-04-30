package io.github.yinjinlong.hnas.controller

import java.net.URLEncoder

/**
 * API前缀
 * @author YJL
 */
object API {

    const val AI = "api/ai"
    const val FILE = "api/file"
    const val USER = "api/user"

    fun http(host: String, path: String, params: Map<String, Any?>? = null): String {
        return "http://$host/$path" +
                if (params.isNullOrEmpty()) ""
                else "?" + params.entries.filter { it.value != null }.joinToString("&") {
                    "${it.key}=${
                        URLEncoder.encode(
                            it.value.toString(),
                            Charsets.UTF_8
                        )
                    }"
                }
    }
}
