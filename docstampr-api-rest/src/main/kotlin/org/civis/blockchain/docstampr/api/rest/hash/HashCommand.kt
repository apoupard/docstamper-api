package org.civis.blockchain.docstampr.api.rest.hash

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import org.civis.blockchain.docstampr.api.document.GitUploadDocument
import org.civis.blockchain.docstampr.api.rest.HashApi
import org.civis.blockchain.docstampr.api.rest.config.DocstamperConfig
import org.civis.blockchain.ssm.client.SsmClient
import org.civis.blockchain.ssm.client.Utils.JsonUtils
import org.civis.blockchain.ssm.client.domain.Context
import org.civis.blockchain.ssm.client.domain.Session
import org.civis.blockchain.ssm.client.domain.SignerAdmin
import org.civis.blockchain.ssm.client.repository.InvokeReturn
import org.civis.blockchain.ssm.client.spring.SsmConfiguration
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.CompletableFuture


@Service
class HashCommand(val hashQuery: HashQuery,
                  val ssmClient: SsmClient,
                  val docstamperConfig: DocstamperConfig,
                  val gitUploadDocument: GitUploadDocument,
                  val signerAdmin: SignerAdmin) {

    fun create(hash: String): CompletableFuture<InvokeReturn>? {
        val hashPosed = hashQuery.find(hash).get()
        if (hashPosed.isPresent) {
            return null;
        }
        val roles = hashMapOf(docstamperConfig.userSigner().name to "DocStampr")
        val session = Session(docstamperConfig.ssmName, hash, "", roles)
        return ssmClient.start(signerAdmin, session)
    }

    fun addMetadata(hash: String, @RequestBody form: HashApi.UploadForm): CompletableFuture<InvokeReturn> {
        val url = uploadToGit(form, hash)
        val context = buildContext(form, url, hash)
        return ssmClient.perform(docstamperConfig.userSigner(), "SetMetadata", context)
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
        val file = toTempFile(metadata.file)
        try {
            return gitUploadDocument.upload(hash, metadata.file.filename(), FileInputStream(file))
        } finally {
            file.delete();
        }
    }

    private fun toTempFile(file: FilePart): File {
        val tmpFile = File.createTempFile(file.filename(), ".temp")
        file.transferTo(tmpFile);
        return tmpFile;
    }

    private fun getIteration(sessionId: String): Int {
        val session = ssmClient.getSession(sessionId).get();
        return session.map { it.iteration }.orElse(0)
    }

}