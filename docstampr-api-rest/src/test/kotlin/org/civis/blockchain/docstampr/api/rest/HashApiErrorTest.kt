package org.civis.blockchain.docstampr.api.rest

import org.assertj.core.api.Assertions.assertThat
import java.util.*



class HashApiErrorTest : WebBaseTest() {

//    @Test
    fun getHash() {
        val hash = UUID.randomUUID().toString()
        val uri = baseUrl().pathSegment("hashes", hash).build().toUri()
        val res = this.restTemplate.getForObject(uri, String::class.java)
        assertThat(res).contains(hash)
    }

//    @Test
    fun getFile() {
        val hash = UUID.randomUUID().toString()
        val uri = baseUrl().pathSegment("hashes/$hash/file").build().toUri()
        val res = this.restTemplate.getForObject(uri, String::class.java)
        assertThat(res).isNotNull()
        assertThat(res!!).isEqualTo("to commit")
    }


}