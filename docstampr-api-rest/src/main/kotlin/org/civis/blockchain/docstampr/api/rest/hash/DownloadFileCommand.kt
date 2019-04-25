package org.civis.blockchain.docstampr.api.rest.hash

import org.civis.blockchain.docstampr.api.rest.HashApi
import org.civis.blockchain.docstampr.api.rest.config.DocstamperConfig
import org.civis.blockchain.docstampr.api.rest.document.GitUploadDocument
import org.civis.blockchain.docstampr.api.rest.exception.NotFoundException
import org.civis.blockchain.ssm.client.domain.SessionState
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class DownloadFileCommand(val hashQuery: HashQuery,
                          val docstamperConfig: DocstamperConfig,
                          val gitUploadDocument: GitUploadDocument) {

    fun downloadFile(hash: String): CompletableFuture<HashApi.HashFileResponse> {
        val state: CompletableFuture<Optional<SessionState>> = hashQuery.find(hash)
        return state.thenApply {  opt ->
            fromGit(opt, hash)
        }
    }

    private fun fromGit(opt: Optional<SessionState>, hash: String): HashApi.HashFileResponse {
        val session: SessionState = opt.orElseThrow {
            throw NotFoundException("$hash not found")
        }
        val filename = session.getFilename()
                ?: throw NotFoundException("$hash don't have file linked")
        val contentType = session.getContentType()
                ?: "application/octet-stream"
        val res = InputStreamResource(gitUploadDocument.get(hash, filename, docstamperConfig.aesSecretKey()))
        return HashApi.HashFileResponse(filename, res, contentType)

    }

}