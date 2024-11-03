package com.yjl.hnas.token

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec

/**
 * @author YJL
 */
object TokenUtils {

    private lateinit var password: String

    private val salt = SecureRandom().generateSeed(8)

    private var iterationCount: Int = 50
    private lateinit var spec: PBEParameterSpec
    private lateinit var key: SecretKey

    internal fun init(
        password: String,
        iterationCount: Int
    ) {
        this.password = password
        this.iterationCount = iterationCount
        val key = PBEKeySpec(password.toCharArray())
        val factory = SecretKeyFactory.getInstance(Token.DEFAULT_ALGORITHM)
        this.key = factory.generateSecret(key)
        spec = PBEParameterSpec(salt, iterationCount)
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
