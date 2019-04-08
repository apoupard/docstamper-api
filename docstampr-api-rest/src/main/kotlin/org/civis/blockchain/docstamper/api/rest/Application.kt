package org.civis.blockchain.docstamper.api.rest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackageClasses = [SignApplication::class])
class SignApplication

fun main(args: Array<String>) {
    runApplication<SignApplication>(*args)
}


