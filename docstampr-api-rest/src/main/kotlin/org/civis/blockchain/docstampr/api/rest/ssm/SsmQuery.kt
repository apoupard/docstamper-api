package org.civis.blockchain.docstampr.api.rest.ssm

import org.civis.blockchain.docstampr.api.rest.config.DocstamperConfig
import org.civis.blockchain.ssm.client.SsmClient
import org.civis.blockchain.ssm.client.domain.Ssm
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class SsmQuery(val ssmClient: SsmClient,
               val docstamperConfig: DocstamperConfig) {

    fun get() : CompletableFuture<Optional<Ssm>> = ssmClient.getSsm(docstamperConfig.ssmName)
}