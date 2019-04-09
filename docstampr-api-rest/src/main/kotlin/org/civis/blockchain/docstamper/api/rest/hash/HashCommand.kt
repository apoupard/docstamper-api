package org.civis.blockchain.docstamper.api.rest.hash

import org.civis.blockchain.docstamper.api.document.GitUploadDocument
import org.civis.blockchain.docstamper.api.rest.HashApi
import org.civis.blockchain.docstamper.api.rest.config.SsmConfig
import org.civis.blockchain.ssm.client.SsmClient
import org.civis.blockchain.ssm.client.Utils.JsonUtils
import org.civis.blockchain.ssm.client.domain.Context
import org.civis.blockchain.ssm.client.domain.Session
import org.civis.blockchain.ssm.client.repository.InvokeReturn
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import java.io.File
import java.io.FileInputStream
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

    fun addMetadata(hash: String, @RequestBody metadata: HashApi.UploadForm): CompletableFuture<InvokeReturn> {
        val url = uploadToGit(metadata, hash)

        val context = Context(hash, Metadata(metadata.tags, url).toJson(), getIteration(hash));
        return ssmClient.perform(ssmConfig.userSigner(), "SetMetadata", context)
    }

    private fun uploadToGit(metadata: HashApi.UploadForm, hash: String): String {
        val file = File.createTempFile(metadata.file.filename(), ".temp")
        metadata.file.transferTo(file);
        return GitUploadDocument(ssmConfig.docstamperGitRepo)
                .upload(hash, metadata.file.filename(), FileInputStream(file))
    }

    private fun getIteration(session: String): Int {
        val session = ssmClient.getSession(session).get();
        return session.map { it.iteration }.orElse(0)
    }

    data class Metadata(val tags : String, val url: String?= null) {
        fun toJson(): String {
            return JsonUtils.toJson(this)
        }
    }

}