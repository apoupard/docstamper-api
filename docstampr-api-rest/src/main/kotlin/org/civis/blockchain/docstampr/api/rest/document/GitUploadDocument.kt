package org.civis.blockchain.docstampr.api.rest.document

import java.io.FileInputStream
import java.io.InputStream
import javax.crypto.SecretKey

class GitUploadDocument(private val docstamprGitRepo: String,
                        private val keyGitRepo: String,
                        private var pushBranch: Boolean) {

    @Synchronized
    fun upload(hash: String, filename: String, data: FileInputStream, key: SecretKey): String {
        val git = GitBaseCommand(docstamprGitRepo, keyGitRepo)
        try {
            git.checkoutBranch(hash)
            git.createFile(filename, data, key)
            val commit = git.commitFile(filename)
            if (pushBranch) {
                git.pushBranch()
            }
            return commit.name
        } finally {
            git.checkoutBranch("master")
        }
    }

    @Synchronized
    fun get(hash: String, filename: String, key: SecretKey): InputStream {
        val git = GitBaseCommand(docstamprGitRepo, keyGitRepo)
        try {
            git.checkoutBranch(hash)
            return git.getFile(filename, key)
        } finally {
            git.checkoutBranch("master")
        }
    }


}