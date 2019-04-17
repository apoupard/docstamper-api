package org.civis.blockchain.docstampr.api.document

import org.civis.blockchain.docstampr.api.rest.crypto.AESCipher
import java.io.FileInputStream

class GitUploadDocument(docstamprGitRepo: String,
                        keyGitRepo: String,
                        var pushBranch: Boolean) {

    val git = GitBaseCommand(docstamprGitRepo, keyGitRepo);

    fun upload(hash: String, filename: String, data: FileInputStream): String {
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