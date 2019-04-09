package org.civis.blockchain.docstampr.api.rest.hash

import org.civis.blockchain.docstampr.api.rest.config.SsmConfig
import org.civis.blockchain.ssm.client.SsmClient
import org.civis.blockchain.ssm.client.domain.SessionState
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class HashQuery(val ssmClient: SsmClient,
                val ssmConfig: SsmConfig) {

    fun list(): CompletableFuture<List<String>> {
        return ssmClient.listSession().thenApply { sessions ->
            sessions.filter(ssmNameFilter(ssmConfig.ssmName))
        }
    }

    private fun ssmNameFilter(ssmToKeep: String): (String) -> Boolean {
        return { sessionId ->
            val session = ssmClient.getSession(sessionId).get()
            session.map { sess -> sess.ssm.equals(ssmToKeep) }.get()
        }
    }

    fun find(name: String): CompletableFuture<Optional<SessionState>> {
        return ssmClient.getSession(name)
    }
}