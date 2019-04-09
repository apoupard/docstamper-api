package org.civis.blockchain.docstampr.api.rest.ssm

import org.civis.blockchain.docstampr.api.rest.config.SsmConfig
import org.civis.blockchain.ssm.client.SsmClient
import org.civis.blockchain.ssm.client.domain.Agent
import org.civis.blockchain.ssm.client.domain.Ssm
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class SsmCommand(val ssmClient: SsmClient,
                 val ssmConfig: SsmConfig) {

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
        return ssmClient.getSsm(ssmConfig.ssmName)
    }

    private fun createSsm(): CompletableFuture<Optional<Ssm>> {
        val admin = ssmConfig.adminSigner()
        val ssm = getWorkflow()
        return ssmClient.create(admin, ssm).thenApply {
            getSsm().get();
        }
    }

    private fun getWorkflow(): Ssm {
        return Ssm(ssmConfig.ssmName, listOf(
                Ssm.Transition(0, 1, "DocStampr", "SetMetadata")
        ))
    }

    private fun getUser(): CompletableFuture<Optional<Agent>> {
        return ssmClient.getAgent(ssmConfig.signerUserName)
    }

    private fun createUser(): CompletableFuture<Optional<Agent>> {
        val admin = ssmConfig.adminSigner()
        val agent = Agent.loadFromFile(ssmConfig.signerUserName, ssmConfig.signerUserFile);
        return ssmClient.registerUser(admin, agent).thenApply {
            getUser().get();
        }
    }

    private fun <T> createIfNotExist(get: () -> CompletableFuture<Optional<T>>, create: () -> CompletableFuture<Optional<T>>): CompletableFuture<Optional<T>> {
        return get().thenApply {
            it.or { create().get() }
        }
    }

}