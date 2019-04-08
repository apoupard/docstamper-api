package org.civis.blockchain.docstamper.api.rest.hash

import org.civis.blockchain.docstamper.api.rest.config.SsmConfig
import org.civis.blockchain.ssm.client.SsmClient
import org.civis.blockchain.ssm.client.domain.Context
import org.civis.blockchain.ssm.client.domain.Session
import org.civis.blockchain.ssm.client.repository.InvokeReturn
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import java.util.concurrent.CompletableFuture

@Service
class HashCommand(val hashQuery: HashQuery,
                  val ssmClient: SsmClient,
                  val ssmConfig: SsmConfig) {

    fun create(hash: String): CompletableFuture<InvokeReturn>? {
        val hashPosed = hashQuery.find(hash).get()
        if(hashPosed.isPresent) {
            return null;
        }
        val admin = ssmConfig.adminSigner()
        val roles = hashMapOf(ssmConfig.userSigner().name to "DocStamper")
        val session = Session(ssmConfig.ssmName, hash, "", roles)
        return ssmClient.start(admin, session)
    }

    fun addMetadata(hash: String, @RequestBody metadata: String): CompletableFuture<InvokeReturn> {
        val context = Context(hash, metadata, getIteration(hash));
        return ssmClient.perform(ssmConfig.userSigner(), "SetMetadata", context)
    }

    private fun getIteration(session: String): Int {
        val session = ssmClient.getSession(session).get();
        return session.map { it.iteration }.orElse(0)
    }


}