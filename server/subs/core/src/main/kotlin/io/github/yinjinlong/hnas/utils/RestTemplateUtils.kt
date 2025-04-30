package io.github.yinjinlong.hnas.utils

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.net.URI

/**
 * @author YJL
 */
fun <D : Any> RestTemplate.get(
    url: String,
    data: D? = null,
    headers: Map<String, String>? = null
): ResponseEntity<Map<*,*>> {
    val req = RequestEntity<Any>(data, HttpHeaders().apply {
        headers?.forEach { (k, v) ->
            this[k] = v
        }
    }, HttpMethod.GET, URI.create(url))
    return exchange(req, Map::class.java)
}
