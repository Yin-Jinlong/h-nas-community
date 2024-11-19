package com.yjl.hnas.token

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * @author YJL
 */
object TokenUtils {

    private lateinit var password: String

    private val salt = ByteArray(16)

    private var iterationCount: Int = 50
    private lateinit var spec: PBEParameterSpec
    private lateinit var key: SecretKey

    @OptIn(ExperimentalEncodingApi::class)
    internal fun init(
        salt: ByteArray,
        password: String,
        iterationCount: Int
    ) {
        if (salt.size != this@TokenUtils.salt.size)
            throw IllegalArgumentException("salt 长度不正确")
        System.arraycopy(salt, 0, this@TokenUtils.salt, 0, this@TokenUtils.salt.size)
        this.password = password
        this.iterationCount = iterationCount
        val key = PBEKeySpec(password.toCharArray())
        val factory = SecretKeyFactory.getInstance(Token.DEFAULT_ALGORITHM)
        this.key = factory.generateSecret(key)
        val ivSpec = IvParameterSpec(this@TokenUtils.salt)
        spec = PBEParameterSpec(this@TokenUtils.salt, iterationCount, ivSpec)
    }

    fun gen(data: ByteArray, algorithm: String): ByteArray {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)
        return cipher.doFinal(data)
    }

    fun decode(encrypted: ByteArray, algorithm: String): ByteArray {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        return cipher.doFinal(encrypted)
    }

}
