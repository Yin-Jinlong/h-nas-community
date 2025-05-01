package io.github.yinjinlong.hnas.tools

import com.google.gson.Gson
import io.github.yinjinlong.spring.boot.util.getLogger
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.beans.factory.annotation.Value
import java.net.URI
import java.net.URLEncoder
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.zip.GZIPInputStream
import javax.net.ssl.HttpsURLConnection
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


/**
 * @author YJL
 */
@ToolService
class WeatherTool(
    @Value("\${qweather.host}")
    private val qweatherHost: String,
    @Value("\${qweather.jwt.privateKey}")
    private val jwtPrivateKey: String,
    @Value("\${qweather.jwt.projectId}")
    private val projectId: String,
    @Value("\${qweather.jwt.keyId}")
    private val jwtKeyId: String,
    val gson: Gson
) : CommonTool(WeatherTool::class.getLogger()) {
    data class GeoResponse(
        val code: Int,
        val location: List<Location>,
        val refer: Refer,
    )

    data class Location(
        val name: String,
        val id: String,
        val lat: Double,
        val lon: Double,
        val adm2: String,
        val adm1: String,
        val country: String,
        val tz: String,
        val utcOffset: String,
        val isDst: Int,
        val type: String,
        val rank: Double,
        val fxLink: String,
    )

    data class WeatherResponse(
        val code: Int,
        val updateTime: String,
        val fxLink: String,
        val now: Weather,
        val refer: Refer,
        val daily: List<Weather>
    )

    data class Refer(
        val sources: List<String>?,
        val license: List<String>?,
    )

    data class Weather(
        val obsTime: String,
        val temp: Double,
        val feelsLike: Double,
        val icon: String,
        val text: String,
        val wind360: Double,
        val windDir: String,
        val windScale: Double,
        val windSpeed: Double,
        val humidity: Double,
        val precip: Double,
        val pressure: Double,
        val vis: Double,
        val cloud: Double?,
        val dew: Double?,
    )

    @OptIn(ExperimentalEncodingApi::class)
    private fun jwt(): String {
        val privateKeyBytes: ByteArray = Base64.decode(jwtPrivateKey)
        val keySpec = PKCS8EncodedKeySpec(privateKeyBytes)
        val keyFactory: KeyFactory = KeyFactory.getInstance("EdDSA")
        val privateKey: PrivateKey = keyFactory.generatePrivate(keySpec)

        // Header
        val headerJson = "{\"alg\": \"EdDSA\", \"kid\": \"$jwtKeyId\"}"

        // Payload
        val iat = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() - 10
        val exp = iat + 900
        val payloadJson = "{\"sub\": \"$projectId\", \"iat\": $iat, \"exp\": $exp}"

        // Base64url header+payload
        val headerEncoded: String = Base64.UrlSafe.encode(headerJson.toByteArray())
        val payloadEncoded: String = Base64.UrlSafe.encode(payloadJson.toByteArray())
        val data = "$headerEncoded.$payloadEncoded"

        // Sign
        val signer: Signature = Signature.getInstance("EdDSA")
        signer.initSign(privateKey)
        signer.update(data.toByteArray())
        val signature: ByteArray = signer.sign()

        val signatureEncoded: String = Base64.UrlSafe.encode(signature)
        return "$data.$signatureEncoded"
    }

    fun Map<String, String?>.toQueryString(): String {
        return entries.filter { (_, v) -> v != null }
            .joinToString("&") { (k, v) -> "$k=${URLEncoder.encode(v, Charsets.UTF_8)}" }
    }

    fun get(path: String, params: Map<String, String?>): String {
        val url =
            URI.create("https://$qweatherHost/$path?${params.toQueryString()}").toURL()
        val conn = url.openConnection() as HttpsURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("Authorization", "Bearer ${jwt()}")
        conn.connect()
        val responseCode = conn.responseCode
        if (responseCode != 200)
            throw IllegalStateException("请求失败：${conn.content}")
        val gzipInputStream = GZIPInputStream(conn.inputStream)
        return gzipInputStream.use { it.reader().readText() }
    }

    @Tool(description = "获取地理信息，包含locationID")
    fun searchLocation(
        @ToolParam(description = "城市名称，中英都可") name: String,
        @ToolParam(description = "城市的上级行政区划", required = false) adm: String? = null,
        @ToolParam(description = "返回结果的数量，取值范围1-20，默认返回10个结果。", required = false) number: Int? = null,
    ): List<Location> {
        logCall(name, adm, number)
        val res = get(
            "geo/v2/city/lookup", mapOf(
                "location" to name,
                "adm" to adm,
                "number" to number?.toString(),
                "lang" to "zh"
            )
        )
        val resp = gson.fromJson(res, GeoResponse::class.java)
        return resp.location
    }

    @Tool(description = "获取天气信息")
    fun getCurrentWeather(
        @ToolParam(description = "locationID或者英文逗号分隔的经纬度（小数点后有效2位）")
        location: String,
    ): Weather {
        logCall(location)
        val res = get(
            "v7/weather/now", mapOf(
                "location" to location,
                "lang" to "zh",
            )
        )
        val resp = gson.fromJson(res, WeatherResponse::class.java)
        return resp.now
    }

}