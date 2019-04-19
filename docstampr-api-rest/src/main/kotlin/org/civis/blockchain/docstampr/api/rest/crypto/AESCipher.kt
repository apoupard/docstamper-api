package org.civis.blockchain.docstampr.api.rest.crypto

import com.google.common.io.ByteStreams
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

class AESCipher {

    private val ALGO = "AES"

    @Throws(NoSuchAlgorithmException::class)
    fun generateSecretKey(): SecretKey {
        val kg = KeyGenerator.getInstance(ALGO)
        kg.init(SecureRandom(byteArrayOf(1, 2, 3)))
        return kg.generateKey()
    }

    fun secretKeyFromBase64(b64Key: String): SecretKey {
        val key = Base64.getDecoder().decode(b64Key)
        return SecretKeySpec(key, ALGO)
    }

    @Throws(CryptoException::class)
    fun encrypt(key: SecretKey, file: File, outputStream: OutputStream) {
        FileInputStream(file).use {fileInput ->
            try {
                encrypt(key, fileInput, outputStream)
            } catch (e: Exception) {
                throw CryptoException("Error encrypting file:" + file.name, e)
            }
        }
    }

    @Throws(CryptoException::class)
    fun encrypt(key: SecretKey, fileInput: InputStream, outputStream: OutputStream) {
        getEncryptCipher(outputStream, key).use { output ->
            try {
                ByteStreams.copy(fileInput, output)
            } catch (e: Exception) {
                throw CryptoException("Error encrypting:", e)
            }
        }
    }

    fun decrypt(fileName: String, key: SecretKey): InputStream {
        try {
            val fileInput = FileInputStream(fileName)
            return getDecryptCipher(key, fileInput)
        } catch (e: Exception) {
            throw CryptoException("Error decrypting file:$fileName", e)
        }

    }

    fun decrypt(fileInput: InputStream, key: SecretKey): InputStream {
        try {
            return getDecryptCipher(key, fileInput)
        } catch (e: Exception) {
            throw CryptoException("Error decrypting file", e)
        }

    }

    @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidKeyException::class)
    private fun getDecryptCipher(key: SecretKey, fileInput: InputStream): CipherInputStream {
        val cipher = Cipher.getInstance(ALGO)
        cipher.init(Cipher.DECRYPT_MODE, key)
        return CipherInputStream(fileInput, cipher)
    }

    @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidKeyException::class)
    private fun getEncryptCipher(fileOutput: OutputStream, key: SecretKey): CipherOutputStream {
        val cipher = Cipher.getInstance(ALGO)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return CipherOutputStream(fileOutput, cipher)
    }

}