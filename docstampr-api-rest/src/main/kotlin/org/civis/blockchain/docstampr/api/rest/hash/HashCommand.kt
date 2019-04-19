package org.civis.blockchain.docstampr.api.rest.hash

import org.civis.blockchain.docstampr.api.rest.HashApi
import org.civis.blockchain.docstampr.api.rest.config.DocstamperConfig
import org.civis.blockchain.docstampr.api.rest.document.GitUploadDocument
import org.civis.blockchain.ssm.client.SsmClient
import org.civis.blockchain.ssm.client.domain.Context
import org.civis.blockchain.ssm.client.domain.Session
import org.civis.blockchain.ssm.client.domain.SignerAdmin
import org.civis.blockchain.ssm.client.repository.InvokeReturn
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import java.io.File
import java.io.FileInputStream
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
            return null
        }
        val roles = hashMapOf(docstamperConfig.userSigner().name to "DocStampr")
        val session = Session(docstamperConfig.ssmName, hash, "", roles)
        return ssmClient.start(signerAdmin, session)
    }

    fun addMetadata(hash: String, @RequestBody form: HashApi.UploadForm): CompletableFuture<InvokeReturn> {
        val commitId = uploadToGit(form, hash)
        val context = buildContext(form, commitId, hash)
        return ssmClient.perform(docstamperConfig.userSigner(), "SetMetadata", context)
    }

    private fun buildContext(form: HashApi.UploadForm, commitId: String?, hash: String): Context {
        var json = form.metadata
        if (form.file != null) {
            json = addMetadata(json, form.file, commitId)
        }
        return Context(hash, json, getIteration(hash))
    }


    private fun uploadToGit(metadata: HashApi.UploadForm, hash: String): String? {
        if (metadata.file == null) {
            return null
        }
        val file = toTempFile(metadata.file)
        try {
            return gitUploadDocument.upload(hash, metadata.file.filename(), FileInputStream(file), docstamperConfig.aesSecretKey())
        } finally {
            file.delete()
        }
    }

    private fun toTempFile(file: FilePart): File {
        val tmpFile = File.createTempFile(file.filename(), ".temp")
        file.transferTo(tmpFile).block()
        return tmpFile
    }

    private fun getIteration(sessionId: String): Int {
        val session = ssmClient.getSession(sessionId).get()
        return session.map { it.iteration }.orElse(0)
    }

}