package org.civis.blockchain.docstampr.api.document

import org.civis.blockchain.ssm.client.Utils.FileUtils
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*

class GitBaseCommandTest {
    val repo = "file:../infra/docstampr-file"
    @Test
    fun test_addFile() {
        val uuid = UUID.randomUUID().toString()
        val file = File(FileUtils.getUrl("fileToCommit.txt").toURI())
        System.out.println(file.absoluteFile)
        GitBaseCommand(repo).checkoutBranch(uuid)
        GitBaseCommand(repo).commitFile(file.name, file.inputStream())
        GitBaseCommand(repo).pushBranch()
        GitBaseCommand(repo).checkoutBranch("master")
    }

}