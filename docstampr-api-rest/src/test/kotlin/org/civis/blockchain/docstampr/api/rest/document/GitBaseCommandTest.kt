package org.civis.blockchain.docstampr.api.document

import org.civis.blockchain.docstampr.api.rest.crypto.AESCipher
import org.civis.blockchain.ssm.client.Utils.FileUtils
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*

class GitBaseCommandTest {

    companion object {
        val REPO = "file:../infra/dev/git/docstampr-file"
        val KEY = ""
        val git = GitBaseCommand(GitBaseCommandTest.REPO, GitBaseCommandTest.KEY)
    }

    @Test
    fun test_commitFile() {
        val uuid = UUID.randomUUID().toString()
        val file = File(FileUtils.getUrl("fileToCommit.txt").toURI())

        git.checkoutBranch("master")
        git.checkoutBranch(uuid)

        git.createFile(file.name, file.inputStream())
        git.commitFile(file.name)

        git.checkoutBranch("master")
    }

    @Test
    fun test_commitEncryptedFile() {
        val uuid = UUID.randomUUID().toString()
        val file = File(FileUtils.getUrl("fileToCommit.txt").toURI())

        git.checkoutBranch("master")
        git.checkoutBranch(uuid)

        git.createFile("fileToCommit.txt.enc", file.inputStream(), AESCipher().generateSecretKey())

        git.commitFile("fileToCommit.txt.enc")

        git.checkoutBranch("master")
    }

}