package org.civis.blockchain.docstampr.api.document

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
    fun test_addFile() {
        git.checkoutBranch("master")
        val uuid = UUID.randomUUID().toString()
        val file = File(FileUtils.getUrl("fileToCommit.txt").toURI())
        System.out.println(file.absoluteFile)
        git.checkoutBranch(uuid)
        git.commitFile(file.name, file.inputStream())
//        GitBaseCommand(GitBaseCommandTest.REPO, GitBaseCommandTest.KEY).pushBranch()
        git.checkoutBranch("master")
    }

}