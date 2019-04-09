package org.civis.blockchain.docstamper.api.document

import java.io.FileInputStream

class GitUploadDocument(docstamperGitRepo: String) {

    val git = GitBaseCommand(docstamperGitRepo);

    fun upload(hash: String, filename: String, data: FileInputStream): String {

        git.checkoutBranch(hash)
        git.commitFile(filename, data)
        git.pushBranch()
        git.checkoutBranch("master")
        return "https://raw.githubusercontent.com/apoupard/docstamper-file/$hash/${filename}"
    }

}