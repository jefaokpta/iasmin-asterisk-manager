package br.com.iasmin.iasminasteriskari

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AmiConnection {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun start() {
        logger.info("\uD83D\uDE80 Ami conectando...")
    }
}