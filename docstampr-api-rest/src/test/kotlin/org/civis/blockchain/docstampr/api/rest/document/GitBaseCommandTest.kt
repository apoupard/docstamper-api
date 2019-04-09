package org.civis.blockchain.docstampr.api.document

import org.civis.blockchain.ssm.client.Utils.FileUtils
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*

class GitBaseCommandTest {

    val repo = "file:../infra/civis-docstampr-file"
    val key = "file:../infra/bc1/id_rsa.civis.github"

    @Test
    fun test_addFile() {
        val uuid = UUID.randomUUID().toString()
        val file = File(FileUtils.getUrl("fileToCommit.txt").toURI())
        System.out.println(file.absoluteFile)
        GitBaseCommand(repo, key).checkoutBranch(uuid)
        GitBaseCommand(repo, key).commitFile(file.name, file.inputStream())
        GitBaseCommand(repo, key).pushBranch()
        GitBaseCommand(repo, key).checkoutBranch("master")
    }

}