package com.example.iasminasteriskari.ari

import ch.loway.oss.ari4java.AriFactory
import ch.loway.oss.ari4java.AriVersion
import ch.loway.oss.ari4java.generated.AriWSHelper
import ch.loway.oss.ari4java.generated.models.ChannelStateChange
import ch.loway.oss.ari4java.generated.models.Message
import ch.loway.oss.ari4java.generated.models.StasisEnd
import ch.loway.oss.ari4java.generated.models.StasisStart
import ch.loway.oss.ari4java.tools.AriConnectionEvent
import ch.loway.oss.ari4java.tools.RestException
import com.example.iasminasteriskari.ari.actions.ActionEnum
import com.example.iasminasteriskari.ari.actions.AriAction
import com.example.iasminasteriskari.ari.actions.RunActionService
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

@Component
class AriConnection(
    private val ariTaskExecutor: ThreadPoolTaskExecutor,
    private val channelStateCache: ChannelStateCache,
    private val runActionService: RunActionService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    @Value("\${ari.base-url}")
    private var urlBase: String = ""
    @Value("\${ari.username}")
    private var username: String = ""
    @Value("\${ari.password}")
    private var password: String = ""
    @Value("\${ari.app-name}")
    private var appName: String = ""

    @PostConstruct
    fun start() {
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
                channelStateCache.addChannelState(
                    ChannelState(
                        channel, mutableListOf(
                            AriAction(ActionEnum.ANSWER),
                            AriAction(ActionEnum.PLAYBACK, listOf("sound:hello-world")),
                            AriAction(ActionEnum.HANGUP)
                        )
                    )
                )
                logger.info("${channel.id} >> Ligacao de ${channel.caller.name} ${channel.caller.number} para ${channel.dialplan.exten} no canal ${channel.name}")
                runActionService.runAction(ari, channel)
//                ari.channels().originate("PJSIP/6002").setApp(appName).setCallerId("Caller identity").setTimeout(30).execute()
            }

            override fun onConnectionEvent(event: AriConnectionEvent) {
                logger.info("Conexao event: {}", event)
            }

            override fun onFailure(e: RestException?) {
                logger.error("Erro conectando to ARI", e)
            }

            override fun onStasisEnd(stasisEnd: StasisEnd) {
                logger.info("${stasisEnd.channel.id} >> Stasis end - Canal: ${stasisEnd.channel.name} desligado")
                channelStateCache.removeChannelState(stasisEnd.channel)
            }

            override fun onChannelStateChange(message: ChannelStateChange) {
                logger.info("${message.channel.id} >> Estado do canal, mudou para ${message.channel.state}")
                channelStateCache.updateChannel(message.channel)
                runActionService.runAction(ari, message.channel)
            }

        })
    }

}