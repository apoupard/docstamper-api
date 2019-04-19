package org.civis.blockchain.docstampr.api.rest.crypto

import com.google.common.io.CharStreams
import com.google.common.io.Files
import org.assertj.core.api.Assertions
import org.civis.blockchain.ssm.client.Utils.FileUtils
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.security.NoSuchAlgorithmException
import java.util.*

class AESCipherTest {

    @Test
    @Throws(IOException::class)
    fun encrypt() {
        val fileToEncrypt = getFile("fileToCommit.txt")
        val encryptedFile = File.createTempFile("enc_", "tmp")
        val encryptedFileProof = getFile("fileToCommit.encrypted")
        try {
            val os = FileOutputStream(encryptedFile)
            val key = AESCipher().secretKeyFromBase64("+cRaRuaSK1/RObE9oEOm6Q==")
            AESCipher().encrypt(key, fileToEncrypt, os)

            val areFileEquals = Files.equal(encryptedFile, encryptedFileProof)
            Assertions.assertThat(areFileEquals).isTrue()
        } finally {
            encryptedFile.delete()
        }
    }

    @Test
    @Throws(IOException::class, NoSuchAlgorithmException::class)
    fun decrypr() {
        val encryptedFile = getFile("fileToCommit.encrypted")
        try {
            val key = AESCipher().secretKeyFromBase64("+cRaRuaSK1/RObE9oEOm6Q==")
            AESCipher().decrypt(encryptedFile.absolutePath, key).use { decryptedStream ->

                val value = CharStreams.toString(InputStreamReader(decryptedStream))
                Assertions.assertThat(value).isEqualTo("to commit")
            }
        } finally {
            encryptedFile.delete()
        }
    }

    @Test
    @Throws(NoSuchAlgorithmException::class)
    fun generateSecretKey() {
        val key = AESCipher().generateSecretKey()
        val encodedKey = Base64.getEncoder().encodeToString(key.encoded)
        println(encodedKey)
        val keyBuilded = AESCipher().secretKeyFromBase64(encodedKey)
        Assertions.assertThat(key).isEqualToComparingFieldByField(keyBuilded)
    }

    @Throws(MalformedURLException::class)
    fun getFile(filename: String): File {
        val url = FileUtils.getUrl(filename)
        return File(url.file)
    }
}