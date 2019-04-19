package org.civis.blockchain.docstampr.api.rest.config

import org.civis.blockchain.docstampr.api.rest.document.GitUploadDocument
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GitConfig (val docstamperConfig: DocstamperConfig) {

    @Bean
    fun get(): GitUploadDocument {
        return GitUploadDocument(
                docstamperConfig.docstamprGitRepo,
                docstamperConfig.docstamprGitKey,
                docstamperConfig.pushGitBranch
        )
    }

}