package br.com.iasmin.iasminasteriskari.ari

import ch.loway.oss.ari4java.AriFactory
import ch.loway.oss.ari4java.AriVersion
import ch.loway.oss.ari4java.generated.AriWSHelper
import ch.loway.oss.ari4java.generated.models.*
import ch.loway.oss.ari4java.tools.AriConnectionEvent
import ch.loway.oss.ari4java.tools.RestException
import br.com.iasmin.iasminasteriskari.ari.actions.ActionEnum
import br.com.iasmin.iasminasteriskari.ari.actions.AriAction
import br.com.iasmin.iasminasteriskari.ari.actions.RunActionService
import br.com.iasmin.iasminasteriskari.ari.channel.ChannelLegEnum
import br.com.iasmin.iasminasteriskari.ari.channel.ChannelState
import br.com.iasmin.iasminasteriskari.ari.channel.ChannelStateCache
import br.com.iasmin.iasminasteriskari.ari.channel.ChannelStateEnum
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class AriConnection(
    private val ariTaskExecutor: ThreadPoolTaskExecutor,
    private val channelStateCache: ChannelStateCache,
    private val runActionService: RunActionService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${ari.host}")
    private var host: String = ""

    @Value("\${ari.user}")
    private var username: String = ""

    @Value("\${ari.password}")
    private var password: String = ""

    @Value("\${ari.outbound-app-name}")
    private var outboundAppName: String = ""

    @PostConstruct
    fun init() {
        logger.info("\uD83D\uDE80 Ari conectando $outboundAppName...")
        connectOutbound()
    }

    private fun connectOutbound() {
        val ari = AriFactory.nettyHttp(
            host,
            username,
            password,
            AriVersion.ARI_8_0_0,
            outboundAppName
        )

        ari.events().eventWebsocket(outboundAppName).execute(object : AriWSHelper() {
            override fun onSuccess(message: Message) {
                ariTaskExecutor.execute { super.onSuccess(message) }
            }

            override fun onStasisStart(stasisStart: StasisStart) {
                if (stasisStart.args.contains(ActionEnum.DIAL_TRUNK.name)) {
                    runActionService.dialTrunkHandler(ari, stasisStart)
                    return
                }
                if (stasisStart.args.contains("record")) {
                    recordChannel(ari, stasisStart.channel.id, stasisStart.args[1])
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
//                            AriAction(ActionEnum.ANSWER),
//                            AriAction(ActionEnum.PLAYBACK, args = listOf("sound:hello-world")),
//                            AriAction(ActionEnum.HANGUP),
//                            AriAction(ActionEnum.DIAL_TRUNK, listOf("IASMIN_JUPITER")),
                            AriAction(ActionEnum.DIAL_TRUNK, listOf("SipTrunk")),
                        )
                    )
                )
                runActionService.runAction(ari, channel, outboundAppName)
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

            override fun onChannelHangupRequest(message: ChannelHangupRequest) {
                logger.warn("${message.channel?.id} >> Canal ${message.channel?.name} recebeu um hangup")
                channelStateCache.getChannelState(message.channel.id)?.let { channelState ->
                    try {
                        if (channelState.channelLegEnum == ChannelLegEnum.A) {
                            channelState.bridgeId?.let { bridgeId -> ari.bridges().destroy(bridgeId).execute() }
                        }
                    } catch (e: RestException) {
                        logger.error("${channelState.channel.id} >> Canal ${channelState.channel.name} tentou destruir bridge já destruida")
                    }
                    try {
                        channelState.connectedChannel?.let { connectedChannel ->
                            ari.channels().hangup(connectedChannel).execute()
                        }
                    } catch (e: RestException) {
                        logger.error("${channelState.channel.id} >> Canal ${channelState.channel.name} tentou desligar ${channelState.connectedChannel} já desligado")
                    }
                }
            }

            override fun onChannelDestroyed(message: ChannelDestroyed) {
                logger.warn("${message.channel?.id} >> Canal ${message.channel?.name} foi destruido")
            }

            override fun onPlaybackFinished(message: PlaybackFinished) {
                logger.warn("Playback ${message.playback.id} terminou")
                channelStateCache.removeActionByActionId(message.playback.id)?.let { action ->
                    runActionService.runAction(ari, action.channel, outboundAppName)
                }
            }

            override fun onChannelStateChange(message: ChannelStateChange) {
                logger.info("${message.channel.id} >> Estado do canal ${message.channel.name}, mudou para: ${message.channel.state}")
                channelStateCache.getChannelState(message.channel.id)?.let { channelState ->
                    if (channelState.channelLegEnum === ChannelLegEnum.B &&
                        message.channel.state.equals(ChannelStateEnum.UP.name, ignoreCase = true)
                    ) {
                        channelStateCache.getChannelState(channelState.connectedChannel!!)?.let { connectedChannel ->
                            recordBridge(ari, connectedChannel.bridgeId!!, connectedChannel.channel.id)
                            createSnoopChannelToRecord(
                                ari,
                                connectedChannel.channel.id,
                                outboundAppName,
                                createRecordName(connectedChannel.channel.id, ChannelLegEnum.A)
                            )
                            createSnoopChannelToRecord(
                                ari,
                                channelState.channel.id,
                                outboundAppName,
                                createRecordName(connectedChannel.channel.id, ChannelLegEnum.B)
                            )
                        }
                    }
                }
            }

            override fun onChannelVarset(message: ChannelVarset) {
                return
            }

            override fun onChannelConnectedLine(message: ChannelConnectedLine) {
                logger.warn("${message.channel.id} >> ConnectedLine atualizado para ${message.channel.connected.number}")
            }

            override fun onChannelCallerId(message: ChannelCallerId) {
                logger.warn("${message.channel.id} >> CallerId atualizado para ${message.channel.caller.number}")
            }

        })
    }
}