package com.yjl.hnas.token

import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec

/**
 * @author YJL
 */
object TokenUtils {

    private lateinit var password: String

    private val salt = ByteArray(16)

    private var iterationCount: Int = 50
    private lateinit var spec: PBEParameterSpec
    private lateinit var key: SecretKey

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

    private fun zip(data: ByteArray): ByteArray {
        val deflater = Deflater(9)
        deflater.setInput(data)
        deflater.finish()

        val res = ByteArrayOutputStream()
        val out = ByteArray(data.size)
        while (!deflater.finished()) {
            val len = deflater.deflate(out)
            res.write(out, 0, len)
        }
        deflater.end()
        return res.toByteArray()
    }

    private fun unzip(data: ByteArray): ByteArray {
        val inflater = Inflater()
        inflater.setInput(data)

        val out = ByteArray(data.size)
        val res = ByteArrayOutputStream()
        while (!inflater.finished()) {
            val len = inflater.inflate(out)
            res.write(out, 0, len)
        }
        inflater.end()
        return res.toByteArray()
    }

    fun gen(data: ByteArray, algorithm: String): ByteArray {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)
        return cipher.doFinal(zip(data))
    }

    fun decode(encrypted: ByteArray, algorithm: String): ByteArray {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        return unzip(cipher.doFinal(encrypted))
    }

}
