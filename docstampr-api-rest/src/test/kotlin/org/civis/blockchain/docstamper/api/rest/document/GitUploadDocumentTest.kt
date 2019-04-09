package org.civis.blockchain.docstamper.api.document

import org.assertj.core.api.Assertions
import org.civis.blockchain.ssm.client.Utils.FileUtils
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*

internal class GitUploadDocumentTest {

    val repo = "file:../infra/docstamper-file"

    @Test
    fun test_addFile() {
        val uuid = UUID.randomUUID().toString()
        val file = File(FileUtils.getUrl("fileToCommit.txt").toURI())
        val url = GitUploadDocument(repo).upload(uuid, file.name, file.inputStream())
        Assertions.assertThat(url)
                .isEqualTo("https://raw.githubusercontent.com/apoupard/docstamper-file/$uuid/fileToCommit.txt")

    }
}