package org.civis.blockchain.docstampr.api.rest.ssm

import org.civis.blockchain.docstampr.api.rest.config.DocstamperConfig
import org.civis.blockchain.ssm.client.SsmClient
import org.civis.blockchain.ssm.client.domain.Agent
import org.civis.blockchain.ssm.client.domain.SignerAdmin
import org.civis.blockchain.ssm.client.domain.Ssm
import org.civis.blockchain.ssm.client.spring.SsmConfiguration
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class SsmCommand(val ssmClient: SsmClient,
                 val docstamperConfig: DocstamperConfig,
                 val signerAdmin: SignerAdmin) {

    fun init(): CompletableFuture<Optional<Ssm>> {
        return initUser().thenApply {
            initSsm().get()
        }
    }

    private fun initSsm(): CompletableFuture<Optional<Ssm>> {
        return createIfNotExist({ getSsm() }, { createSsm() })
    }

    private fun initUser(): CompletableFuture<Optional<Agent>> {
        return createIfNotExist({ getUser() }, { createUser() })
    }

    private fun getSsm(): CompletableFuture<Optional<Ssm>> {
        return ssmClient.getSsm(docstamperConfig.ssmName)
    }

    private fun createSsm(): CompletableFuture<Optional<Ssm>> {

        val ssm = getWorkflow()
        return ssmClient.create(signerAdmin, ssm).thenApply {
            getSsm().get();
        }
    }

    private fun getWorkflow(): Ssm {
        return Ssm(docstamperConfig.ssmName, listOf(
                Ssm.Transition(0, 0, "DocStampr", "SetMetadata")
        ))
    }

    private fun getUser(): CompletableFuture<Optional<Agent>> {
        return ssmClient.getAgent(docstamperConfig.signerUserName)
    }

    private fun createUser(): CompletableFuture<Optional<Agent>> {
        val agent = Agent.loadFromFile(docstamperConfig.signerUserName, docstamperConfig.signerUserFile);
        return ssmClient.registerUser(signerAdmin, agent).thenApply {
            getUser().get();
        }
    }

    private fun <T> createIfNotExist(get: () -> CompletableFuture<Optional<T>>, create: () -> CompletableFuture<Optional<T>>): CompletableFuture<Optional<T>> {
        return get().thenApply {
            if (it.isPresent) {
                it
            } else {
                create().get()
            }
        }
    }

}