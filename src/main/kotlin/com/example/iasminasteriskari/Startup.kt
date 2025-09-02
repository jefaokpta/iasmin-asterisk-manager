package com.example.iasminasteriskari

import ch.loway.oss.ari4java.ARI
import ch.loway.oss.ari4java.AriFactory
import ch.loway.oss.ari4java.AriVersion
import ch.loway.oss.ari4java.generated.AriWSHelper
import ch.loway.oss.ari4java.generated.models.ChannelStateChange
import ch.loway.oss.ari4java.generated.models.Message
import ch.loway.oss.ari4java.generated.models.StasisEnd
import ch.loway.oss.ari4java.generated.models.StasisStart
import ch.loway.oss.ari4java.tools.AriConnectionEvent
import ch.loway.oss.ari4java.tools.RestException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit


@Component
class Startup(private val ariTaskExecutor: ThreadPoolTaskExecutor) {
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
        val ari = AriFactory.nettyHttp(
            urlBase,
            username,
            password,
            AriVersion.ARI_8_0_0,
            appName
        )

        ari.events().eventWebsocket(appName).execute(object : AriWSHelper() {
            override fun onSuccess(message: Message) {
                ariTaskExecutor.execute { super.onSuccess(message) }
            }

            override fun onStasisStart(stasisStart: StasisStart) {
                val channel = stasisStart.channel
                logger.info("${channel.id} >> ${channel.name}")
//                ari.channels().originate("PJSIP/6002").setApp(appName).setCallerId("Caller identity").setTimeout(30).execute()
                ari.channels().answer(channel.id).execute()
                TimeUnit.SECONDS.sleep(1)
                ari.channels().play(channel.id, "sound:hello-world").execute()
                TimeUnit.SECONDS.sleep(3)
                ari.channels().hangup(channel.id).execute()
            }

            override fun onConnectionEvent(event: AriConnectionEvent) {
                logger.info("Conexao event: {}", event)
            }

            override fun onFailure(e: RestException?) {
                logger.error("Erro conectando to ARI", e)
            }

            override fun onStasisEnd(stasisEnd: StasisEnd) {
                logger.info("${stasisEnd.channel.id} >> Stasis end - Canal: ${stasisEnd.channel.name} desligado")
            }

            override fun onChannelStateChange(message: ChannelStateChange) {
                logger.warn("Channel state change: ${message.channel.id} - ${message.channel.state}")
            }
        })
    }
}