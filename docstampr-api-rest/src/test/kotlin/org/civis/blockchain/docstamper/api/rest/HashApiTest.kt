package org.civis.blockchain.docstamper.api.rest

import org.assertj.core.api.Assertions.assertThat
import org.civis.blockchain.ssm.client.domain.Ssm
import org.civis.blockchain.ssm.client.repository.InvokeReturn
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.http.HttpEntity
import java.util.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class HashApiTest() : WebBaseTest() {

    companion object {
        val hash = UUID.randomUUID().toString()
    }

    @Test
    @Order(10)
    fun fullTest() {
        getNullSsm()
        initSsm()
        getSsm()
        addHash()
        getHash()
    }

    fun getNullSsm() {
        try {
            val uri = baseUrl().pathSegment("ssm").build().toUri()
            val res = this.restTemplate.getForObject(uri, Ssm::class.java)
//            assertThat(res).isNull()
        } catch (ex: Exception) {

        }
    }

    fun initSsm() {
        val uri = baseUrl().path("ssm").build().toUri()
        val res : Ssm? = this.restTemplate.postForObject(uri, HttpEntity(hash), Ssm::class.java)
        assertThat(res).isNotNull
        assertThat(res!!.name).isEqualTo("docstamper")
    }

    fun getSsm() {
        val uri = baseUrl().pathSegment("ssm").build().toUri()
        val res = this.restTemplate.getForObject(uri, Ssm::class.java)
        assertThat(res).isNotNull
        assertThat(res!!.name).isEqualTo("docstamper")
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

}