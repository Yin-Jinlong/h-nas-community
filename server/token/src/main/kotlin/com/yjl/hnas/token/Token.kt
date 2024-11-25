package com.yjl.hnas.token

import com.google.gson.Gson
import com.yjl.hnas.utils.base64Url
import com.yjl.hnas.utils.unBase64UrlBytes
import io.github.yinjinlong.md.sha256
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.reflect.KClass

/**
 * @author YJL
 */
class Token<T> private constructor(
    val token: String,
    val encode: EncodeType,
    val time: Calendar,
    val data: T,
) {

    companion object {

        lateinit var gson: Gson

        const val SeeddKey = "hnas.token.seed"
        const val PasswordKey = "hnas.token.password"

        var DEFAULT_ALGORITHM: String = "PBEWITHHMACSHA256ANDAES_256"

        fun init(
            gson: Gson,
        ) {
            this.gson = gson
            val seedStr = System.getProperty(SeeddKey) ?: "yjl"
            TokenUtils.init(
                seedStr.sha256.slice(0..<16).toByteArray(),
                System.getProperty(PasswordKey) ?: "yjl",
                100
            )
            System.clearProperty(SeeddKey)
            System.clearProperty(PasswordKey)
        }

        private fun encodeData(data: ByteArray, encode: EncodeType): String {
            return when (encode) {
                EncodeType.BASE64 -> data.base64Url
            }
        }

        fun <T> gen(
            data: T,
            time: Calendar,
            encode: EncodeType = EncodeType.BASE64
        ): Token<T> {
            val dbs = gson.toJson(data).toByteArray()
            val buf = ByteBuffer.allocate(8 + dbs.size)
            buf.putLong(time.timeInMillis)
            buf.put(dbs)
            return Token(
                token = encodeData(TokenUtils.gen(buf.array(), DEFAULT_ALGORITHM), encode),
                encode = encode,
                time = time,
                data = data
            )

        }

        private fun dataRaw(token: String, encode: EncodeType): ByteArray {
            return when (encode) {
                EncodeType.BASE64 -> token.unBase64UrlBytes
            }
        }


        fun <T : Any> from(token: String, clazz: KClass<T>, encode: EncodeType = EncodeType.BASE64): Token<T> {
            val dataRaw = TokenUtils.decode(dataRaw(token, encode), DEFAULT_ALGORITHM)
            val buf = ByteBuffer.wrap(dataRaw)
            val time = buf.getLong()
            val dataBs = ByteArray(buf.remaining())
            buf.get(dataBs)
            return Token(
                token = token,
                encode = encode,
                time = Calendar.Builder()
                    .setInstant(time)
                    .build(),
                data = gson.fromJson(String(dataBs), clazz.java)
            )
        }

    }

    enum class EncodeType {
        BASE64
    }

    val isAvailable: Boolean
        get() = time.timeInMillis > System.currentTimeMillis()

    /**
     * @param dt 时间误差，最多可以过期多长时间
     */
    fun isAvailable(dt: Long = 1, unit: TimeUnit = TimeUnit.MINUTES): Boolean {
        val dtMs = unit.toMillis(dt)
        return time.timeInMillis + dtMs > System.currentTimeMillis()
    }

}