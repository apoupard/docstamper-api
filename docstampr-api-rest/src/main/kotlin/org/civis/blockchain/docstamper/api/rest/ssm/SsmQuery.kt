package org.civis.blockchain.docstamper.api.rest.ssm

import org.civis.blockchain.docstamper.api.rest.config.SsmConfig
import org.civis.blockchain.ssm.client.SsmClient
import org.civis.blockchain.ssm.client.domain.Ssm
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class SsmQuery(val ssmClient: SsmClient,
               val ssmConfig: SsmConfig) {

    fun get() : CompletableFuture<Optional<Ssm>> = ssmClient.getSsm(ssmConfig.ssmName)
}