package org.civis.blockchain.docstamper.api.rest.config

import org.civis.blockchain.ssm.client.SsmClient
import org.civis.blockchain.ssm.client.SsmClientConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfig(val ssmConfig: SsmConfig) {

    @Bean
    fun ssmClientConfig(): SsmClientConfig {
        return SsmClientConfig(ssmConfig.coopRestUrl);
    }

    @Bean
    fun ssmClient(ssmClientConfig: SsmClientConfig): SsmClient {
        return SsmClient.fromConfig(ssmClientConfig)
    }
}