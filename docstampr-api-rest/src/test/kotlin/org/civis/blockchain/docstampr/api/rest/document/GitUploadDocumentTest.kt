package org.civis.blockchain.docstampr.api.document

import org.assertj.core.api.Assertions
import org.civis.blockchain.ssm.client.Utils.FileUtils
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*

class GitUploadDocumentTest {

    companion object {
        val gitUploadDocument = GitUploadDocument(GitBaseCommandTest.REPO, GitBaseCommandTest.KEY, false)
    }

    @Test
    fun test_addFile() {
        val uuid = UUID.randomUUID().toString()
        val file = File(FileUtils.getUrl("fileToCommit.txt").toURI())
        val url = gitUploadDocument.upload(uuid, file.name, file.inputStream())
        Assertions.assertThat(url)
                .isEqualTo("https://raw.githubusercontent.com/civis-blockchain/docstampr-file/$uuid/fileToCommit.txt.encrypted")
    }
}