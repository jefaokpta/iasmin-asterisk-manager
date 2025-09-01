package com.example.iasminasteriskari

import ch.loway.oss.ari4java.ARI
import ch.loway.oss.ari4java.AriVersion
import ch.loway.oss.ari4java.generated.AriWSHelper
import ch.loway.oss.ari4java.generated.models.Message
import ch.loway.oss.ari4java.generated.models.StasisStart
import ch.loway.oss.ari4java.tools.AriConnectionEvent
import ch.loway.oss.ari4java.tools.RestException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component


@Component
class Startup() {
    private val logger = LoggerFactory.getLogger(Startup::class.java)
    @Value("\${ari.base-url}")
    private var urlBase: String = ""
    @Value("\${ari.username}")
    private var username: String = ""
    @Value("\${ari.password}")
    private var password: String = ""
    @Value("\${ari.app-name}")
    private var appName: String = ""

    @EventListener(ApplicationReadyEvent::class)
    fun onReady() {
        logger.info("Ari conectando...")
        connect()
    }

    private fun connect() {
        val ari = ARI.build(
            urlBase,
            appName,
            username,
            password,
            AriVersion.ARI_8_0_0
        )

        ari.events().eventWebsocket(appName).execute(object : AriWSHelper() {
            override fun onSuccess(message: Message?) {
                logger.info("onSuccess - {}", message)
            }

            protected override fun onStasisStart(message: StasisStart) {
                logger.info("StasisStart - {}", message)
            }

            override fun onConnectionEvent(event: AriConnectionEvent?) {
                logger.info("Conexao event: {}", event)
            }

            override fun onFailure(e: RestException?) {
                logger.error("Erro conectando to ARI", e)
            }
        })
    }
}