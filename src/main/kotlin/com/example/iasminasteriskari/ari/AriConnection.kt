package com.example.iasminasteriskari.ari

import ch.loway.oss.ari4java.ARI
import ch.loway.oss.ari4java.AriFactory
import ch.loway.oss.ari4java.AriVersion
import ch.loway.oss.ari4java.generated.AriWSHelper
import ch.loway.oss.ari4java.generated.models.Channel
import ch.loway.oss.ari4java.generated.models.ChannelCallerId
import ch.loway.oss.ari4java.generated.models.ChannelConnectedLine
import ch.loway.oss.ari4java.generated.models.ChannelStateChange
import ch.loway.oss.ari4java.generated.models.ChannelVarset
import ch.loway.oss.ari4java.generated.models.Message
import ch.loway.oss.ari4java.generated.models.PlaybackFinished
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
        logger.info("\uD83D\uDE80 Ari conectando...")
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
                //TODO: e se deixar na thread original? testar carga no SIPP
                ariTaskExecutor.execute { super.onSuccess(message) }
            }

            override fun onStasisStart(stasisStart: StasisStart) {
                if (stasisStart.args.contains("dialed")) {
                    logger.warn("Canal discado")
                    return
                }
                val channel = stasisStart.channel
                logger.info("${channel.id} >> Ligacao de ${channel.caller.name} ${channel.caller.number} para ${channel.dialplan.exten} no canal ${channel.name}")
                channelStateCache.addChannelState(
                    ChannelState(
                        controlNumber = stasisStart.args[0],
                        peerDDR = stasisStart.args[1],
                        channel = channel,
                        actions = mutableListOf(
                            AriAction(ActionEnum.ANSWER),
                            AriAction(ActionEnum.PLAYBACK, args = listOf("sound:hello-world")),
//                            AriAction(ActionEnum.DIAL_TRUNK, args = listOf("IASMIN_JUPITER", stasisStart.args[1])),
                            AriAction(ActionEnum.HANGUP)
                        )
                    )
                )
                runActionService.runAction(ari, channel, appName)
            }

            override fun onConnectionEvent(event: AriConnectionEvent) {
                logger.info("Conexao ARI: {}", event)
            }

            override fun onFailure(e: RestException?) {
                logger.error("Erro conectando to ARI", e)
            }

            override fun onStasisEnd(stasisEnd: StasisEnd) {
                logger.info("${stasisEnd.channel.id} >> Stasis end - Canal: ${stasisEnd.channel.name} desligado")
                channelStateCache.removeChannelState(stasisEnd.channel)
            }

            override fun onPlaybackFinished(message: PlaybackFinished) {
                logger.warn("Playback ${message.playback.id} terminou")
                channelStateCache.removeActionByActionId(message.playback.id)?.let { action ->
                    runActionService.runAction(ari, action.channel, appName)
                }
            }

            override fun onChannelStateChange(message: ChannelStateChange) {
                logger.info("${message.channel.id} >> Estado do canal, mudou para ${message.channel.state}")
                messageWithChannelHandler(message.channel, ari)
            }

            override fun onChannelVarset(message: ChannelVarset) {
                logger.warn("${message.channel.id} >> Variavel ${message.variable} foi setada para ${message.value}")
                messageWithChannelHandler(message.channel, ari)
            }

            override fun onChannelConnectedLine(message: ChannelConnectedLine) {
                logger.warn("${message.channel.id} >> ConnectedLine atualizado para ${message.channel.connected.number}")
                messageWithChannelHandler(message.channel, ari)
            }

            override fun onChannelCallerId(message: ChannelCallerId) {
                logger.warn("${message.channel.id} >> CallerId atualizado para ${message.channel.caller.number}")
                messageWithChannelHandler(message.channel, ari)
            }

        })
    }

    private fun messageWithChannelHandler(channel: Channel, ari: ARI) {
        channelStateCache.updateChannel(channel)
        runActionService.runAction(ari, channel, appName)
    }

}