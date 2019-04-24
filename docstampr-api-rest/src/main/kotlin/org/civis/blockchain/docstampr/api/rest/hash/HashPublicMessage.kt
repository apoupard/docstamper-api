package org.civis.blockchain.docstampr.api.rest.hash

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import org.civis.blockchain.ssm.client.Utils.JsonUtils
import org.civis.blockchain.ssm.client.domain.SessionState
import org.springframework.http.codec.multipart.FilePart

fun addMetadata(publicMessage: String, filePart: FilePart, commitId: String?): String {
    val json = JsonUtils.toObject(publicMessage, jacksonTypeRef<HashMap<String, String>>())
    json["filename"] = filePart.filename()

    val contentType = filePart.headers().contentType ?: "application/octet-stream"
    json["contentType"] = contentType.toString()

    if (commitId != null) {
        json["commitId"] = commitId
    }
    return JsonUtils.toJson(json).orEmpty()
}

fun SessionState.getFilename(): String {
    val json = JsonUtils.toObject(this.public, jacksonTypeRef<HashMap<String, String>>())
    return json["filename"] ?: "filename"
}

fun SessionState.getContentType(): String {
    val json = JsonUtils.toObject(this.public, jacksonTypeRef<HashMap<String, String>>())
    return json["contentType"] ?: "application/octet-stream"
}