package org.civis.blockchain.docstamper.api.rest.config

import org.civis.blockchain.ssm.client.domain.Signer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class SsmConfig {

    @Value("\${ssm.coop.rest.url}")
    lateinit var coopRestUrl: String

    @Value("\${ssm.name}")
    lateinit var ssmName: String

    @Value("\${ssm.signer.admin.name}")
    lateinit var signerAdminName: String

    @Value("\${ssm.signer.admin.file}")
    lateinit var signerAdminFile: String

    @Value("\${ssm.signer.user.name}")
    lateinit var signerUserName: String

    @Value("\${ssm.signer.user.file}")
    lateinit var signerUserFile: String

    fun adminSigner(): Signer {
        return Signer.loadFromFile(signerAdminName, signerAdminFile);
    }

    fun userSigner(): Signer {
        return Signer.loadFromFile(signerUserName, signerUserFile);
    }

}