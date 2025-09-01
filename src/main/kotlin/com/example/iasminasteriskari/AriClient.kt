package com.example.iasminasteriskari

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class AriClient(
) {
    private val log = LoggerFactory.getLogger(AriClient::class.java)
    private val client: RestClient = RestClient.builder()
        .baseUrl("http://bla.com")
        .build()

    fun answer(channelId: String) {
        log.info("Answering channel {}", channelId)
        client.post()
            .uri("/channels/{id}/answer", channelId)
            .headers { it.setBasicAuth("username", "password") }
            .retrieve()
            .toBodilessEntity()
    }

    fun hangup(channelId: String) {
        log.info("Hanging up channel {}", channelId)
        client.delete()
            .uri("/channels/{id}", channelId)
            .headers { it.setBasicAuth("username", "password") }
            .retrieve()
            .toBodilessEntity()
    }
}
