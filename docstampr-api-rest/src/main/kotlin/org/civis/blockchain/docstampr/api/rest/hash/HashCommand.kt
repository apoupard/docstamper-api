package org.civis.blockchain.docstampr.api.rest.hash

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import org.civis.blockchain.docstampr.api.document.GitUploadDocument
import org.civis.blockchain.docstampr.api.rest.HashApi
import org.civis.blockchain.docstampr.api.rest.config.SsmConfig
import org.civis.blockchain.ssm.client.SsmClient
import org.civis.blockchain.ssm.client.Utils.JsonUtils
import org.civis.blockchain.ssm.client.domain.Context
import org.civis.blockchain.ssm.client.domain.Session
import org.civis.blockchain.ssm.client.repository.InvokeReturn
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.CompletableFuture


@Service
class HashCommand(val hashQuery: HashQuery,
                  val ssmClient: SsmClient,
                  val ssmConfig: SsmConfig) {

    fun create(hash: String): CompletableFuture<InvokeReturn>? {
        val hashPosed = hashQuery.find(hash).get()
        if (hashPosed.isPresent) {
            return null;
        }
        val roles = hashMapOf(ssmConfig.userSigner().name to "DocStampr")
        val session = Session(ssmConfig.ssmName, hash, "", roles)
        return ssmClient.start(ssmConfig.adminSigner(), session)
    }

    fun addMetadata(hash: String, @RequestBody form: HashApi.UploadForm): CompletableFuture<InvokeReturn> {
            val session = ssmClient.getSession(hash).get()

        val url = uploadToGit(form, hash)
        val context = buildContext(form, url, hash)
        return ssmClient.perform(ssmConfig.userSigner(), "SetMetadata", context)
    }

    private fun buildContext(form: HashApi.UploadForm, url: String?, hash: String): Context {
        var json = form.metadata
        if (url != null) {
            json = addUrl(form.metadata, url)
        }
        return Context(hash, json, getIteration(hash))
    }

    private fun addUrl(metadata: String, url: String): String {
        val json = JsonUtils.toObject(metadata, jacksonTypeRef<HashMap<String, String>>())
        json.put("url", url)
        return JsonUtils.toJson(json)
    }

    private fun uploadToGit(metadata: HashApi.UploadForm, hash: String): String? {
        if (metadata.file == null) {
            return null
        }
        val file = File.createTempFile(metadata.file.filename(), ".temp")
        try {
            metadata.file.transferTo(file);
            return GitUploadDocument(ssmConfig.docstamprGitRepo, ssmConfig.docstamprGitKey, ssmConfig.pushGitBranch)
                    .upload(hash, metadata.file.filename(), FileInputStream(file))
        } finally {
            file.delete();
        }
    }

    private fun getIteration(session: String): Int {
        val session = ssmClient.getSession(session).get();
        return session.map { it.iteration }.orElse(0)
    }

}