package org.civis.blockchain.docstamper.api.rest

import org.civis.blockchain.docstamper.api.rest.ssm.SsmCommand
import org.civis.blockchain.docstamper.api.rest.ssm.SsmQuery
import org.civis.blockchain.ssm.client.domain.Ssm
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.concurrent.CompletableFuture

@RestController
class SsmApi(val ssmQuery: SsmQuery,
             val ssmCommand: SsmCommand) {

    @GetMapping("/ssm")
    fun getSsm(): CompletableFuture<Optional<Ssm>> = ssmQuery.get()

    @PostMapping("/ssm")
    fun init(): CompletableFuture<Optional<Ssm>> = ssmCommand.init()
}