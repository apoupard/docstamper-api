package org.civis.blockchain.docstampr.api.rest

import org.assertj.core.api.Assertions.assertThat
import org.civis.blockchain.ssm.client.Utils.FileUtils
import org.civis.blockchain.ssm.client.domain.Ssm
import org.civis.blockchain.ssm.client.repository.InvokeReturn
import org.junit.jupiter.api.Test
import org.springframework.core.io.InputStreamResource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.util.*



class HashApiTest : WebBaseTest() {

    companion object {
        val hash = UUID.randomUUID().toString()
    }

    @Test
    fun fullTest() {
        getNullSsm()
        initSsm()
        getSsm()
        addHash()
        getHash()
        sendMetadata()
        getFile()
    }

    fun getNullSsm() {
        try {
            val uri = baseUrl().pathSegment("ssm").build().toUri()
            this.restTemplate.getForObject(uri, Ssm::class.java)
//            assertThat(res).isNull()
        } catch (ex: Exception) {

        }
    }

    fun initSsm() {
        val uri = baseUrl().path("ssm").build().toUri()
        val res : Ssm? = this.restTemplate.postForObject(uri, HttpEntity(hash), Ssm::class.java)
        assertThat(res).isNotNull
        assertThat(res!!.name).isEqualTo("docstampr")
    }

    fun getSsm() {
        val uri = baseUrl().pathSegment("ssm").build().toUri()
        val res = this.restTemplate.getForObject(uri, Ssm::class.java)
        assertThat(res).isNotNull
        assertThat(res!!.name).isEqualTo("docstampr")
    }

    fun addHash() {
        val uri = baseUrl().path("hashes").build().toUri()
        val res : InvokeReturn? = this.restTemplate.postForObject(uri, HttpEntity(hash), InvokeReturn::class.java)
        assertThat(res).isNotNull
        assertThat(res!!.status).isEqualTo("SUCCESS")
        assertThat(res.transactionId).isNotBlank()
    }

    fun getHash() {
        val uri = baseUrl().pathSegment("hashes", hash).build().toUri()
        val res = this.restTemplate.getForObject(uri, String::class.java)
        assertThat(res).contains(hash)
    }

    private fun sendMetadata() {
        val uri = baseUrl().path("hashes/$hash/metadata").build().toUri()
        val header = HttpHeaders()
        header.contentType = MediaType.MULTIPART_FORM_DATA
        val multipartRequest: MultiValueMap<String, Any> = LinkedMultiValueMap()

        val jsonHeader = HttpHeaders()
        jsonHeader.contentType = MediaType.TEXT_PLAIN
        jsonHeader.contentDisposition = ContentDisposition.builder("form-data")
                .name("metadata").build()

        val jsonPart = HttpEntity("{\"author\":\"qwd\",\"description\":\"qwd\",\"version\":\"qwd\"}", jsonHeader)

        val pictureHeader = HttpHeaders()
        pictureHeader.contentType = MediaType.APPLICATION_OCTET_STREAM
        pictureHeader.contentDisposition = ContentDisposition.builder("form-data")
                .name("file").filename("fileToCommit.txt").build()

        val picturePart = HttpEntity(getTestFile(), pictureHeader)

        multipartRequest.add("metadata", jsonPart)
        multipartRequest.add("file", picturePart)
        val requestEntity = HttpEntity(multipartRequest, header)
        val res = restTemplate.postForObject(uri, requestEntity, InvokeReturn::class.java)
        assertThat(res).isNotNull
        assertThat(res!!.status).isEqualTo("SUCCESS")
        assertThat(res.transactionId).isNotBlank()
    }

    private fun getTestFile(): InputStreamResource {
        return InputStreamResource(FileUtils.getUrl("fileToCommit.txt").openStream())
    }

    fun getFile() {
        val uri = baseUrl().pathSegment("hashes/$hash/file").build().toUri()
        val res = this.restTemplate.getForObject(uri, String::class.java)
        assertThat(res).isNotNull()
        assertThat(res!!).isEqualTo("to commit")
    }


}