package org.civis.blockchain.docstampr.api.rest.config

import org.civis.blockchain.docstampr.api.rest.crypto.AESCipher
import org.civis.blockchain.ssm.client.domain.Signer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import javax.crypto.SecretKey

@Configuration
class DocstamperConfig {

    @Value("\${docstampr.aes.key}")
    lateinit var docstamprAesKey: String

    @Value("\${docstampr.git.repo}")
    lateinit var docstamprGitRepo: String

    @Value("\${docstampr.git.key}")
    lateinit var docstamprGitKey: String

    @Value("\${docstampr.git.push}")
    var pushGitBranch: Boolean = true

    @Value("\${ssm.name}")
    lateinit var ssmName: String

    @Value("\${ssm.signer.user.name}")
    lateinit var signerUserName: String

    @Value("\${ssm.signer.user.key}")
    lateinit var signerUserFile: String

    fun userSigner(): Signer {
        return Signer.loadFromFile(signerUserName, signerUserFile)
    }

    fun aesSecretKey(): SecretKey {
        return AESCipher().secretKeyFromBase64(docstamprAesKey)
    }


}