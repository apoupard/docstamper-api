package org.civis.blockchain.docstampr.api.rest

import org.civis.blockchain.docstampr.api.rest.hash.HashCommand
import org.civis.blockchain.docstampr.api.rest.hash.HashQuery
import org.civis.blockchain.ssm.client.domain.SessionState
import org.civis.blockchain.ssm.client.repository.InvokeReturn
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.CompletableFuture

@RestController
class HashApi(val hashQuery: HashQuery,
              val hashCommand: HashCommand) {

    @GetMapping("/hashes")
    fun getAllHash(): CompletableFuture<List<String>>? = hashQuery.list()

    @GetMapping("/hashes/{hash}")
    fun getSession(@PathVariable hash: String): CompletableFuture<Optional<SessionState>>? = hashQuery.find(hash)

    @PostMapping("/hashes")
    fun createSession(@RequestBody hash: String): CompletableFuture<InvokeReturn>? = hashCommand.create(hash)

    @PostMapping("/hashes/{hash}/metadata")
    fun updateFile(@PathVariable hash: String, @ModelAttribute metadata: UploadForm): CompletableFuture<InvokeReturn>
            = hashCommand.addMetadata(hash, metadata)

    data class UploadForm(val metadata : String, val file: FilePart?);

}