package org.civis.blockchain.docstampr.api.document

import org.civis.blockchain.docstampr.api.rest.crypto.AESCipher
import java.io.FileInputStream

class GitUploadDocument(private val docstamprGitRepo: String,
                        private val keyGitRepo: String,
                        private var pushBranch: Boolean) {

    @Synchronized
    fun upload(hash: String, filename: String, data: FileInputStream): String {
        val git = GitBaseCommand(docstamprGitRepo, keyGitRepo);

        val encryptedFileName = filename + ".encrypted";
        val encryptKey = AESCipher().secretKeyFromBase64("+cRaRuaSK1/RObE9oEOm6Q==");

        git.checkoutBranch(hash)
        git.createFile(encryptedFileName, data, encryptKey)
        git.commitFile(encryptedFileName)
        if (pushBranch) {
            git.pushBranch()
        }

        git.checkoutBranch("master")
        return "https://raw.githubusercontent.com/civis-blockchain/docstampr-file/$hash/${encryptedFileName}"
    }


}