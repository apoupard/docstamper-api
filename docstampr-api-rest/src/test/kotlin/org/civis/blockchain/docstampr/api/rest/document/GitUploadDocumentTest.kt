package org.civis.blockchain.docstampr.api.rest.document

import com.google.common.io.CharStreams
import org.assertj.core.api.Assertions
import org.civis.blockchain.docstampr.api.rest.crypto.AESCipher
import org.civis.blockchain.ssm.client.Utils.FileUtils
import org.junit.jupiter.api.Test
import java.io.File
import java.io.InputStreamReader
import java.util.*

class GitUploadDocumentTest {

    companion object {
        val gitUploadDocument = GitUploadDocument(GitBaseCommandTest.REPO, GitBaseCommandTest.KEY, false)
    }

    @Test
    fun test_addFile() {
        val uuid = UUID.randomUUID().toString()
        val file = File(FileUtils.getUrl("fileToCommit.txt").toURI())
        val url = gitUploadDocument.upload(uuid, file.name, file.inputStream(), AESCipher().generateSecretKey())
        Assertions.assertThat(url)
                .isNotEmpty()
    }

    @Test
    fun test_getFile() {
        val key = AESCipher().generateSecretKey()
        val uuid = UUID.randomUUID().toString()
        val filename = "fileToCommit.txt"
        val file = File(FileUtils.getUrl(filename).toURI())
        gitUploadDocument.upload(uuid, file.name, file.inputStream(),key)

        val stream = gitUploadDocument.get(uuid, filename, key)
        val value = CharStreams.toString(InputStreamReader(stream))
        Assertions.assertThat(value)
                .isEqualTo("to commit")
    }
}