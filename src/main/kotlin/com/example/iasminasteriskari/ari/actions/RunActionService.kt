package com.example.iasminasteriskari.ari.actions

import ch.loway.oss.ari4java.ARI
import ch.loway.oss.ari4java.generated.models.Channel
import ch.loway.oss.ari4java.generated.models.StasisStart
import com.example.iasminasteriskari.ari.channel.ChannelLegEnum
import com.example.iasminasteriskari.ari.channel.ChannelState
import com.example.iasminasteriskari.ari.channel.ChannelStateCache
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RunActionService(private val channelStateCache: ChannelStateCache) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun runAction(ari: ARI, channel: Channel, appName: String) {
        val channelState = channelStateCache.getChannelState(channel.id) ?: return
        val action = channelState.actions.removeFirstOrNull() ?: return
        when (action.type) {
            ActionEnum.HANGUP -> ari.channels().hangup(channel.id).execute()
            ActionEnum.ANSWER -> {
                ari.channels().answer(channel.id).execute()
                runAction(ari, channel, appName)
            }
            ActionEnum.SET_VARIABLE -> {
                ari.channels().setChannelVar(channel.id, action.args[0]).setValue(action.args[1]).execute()
                runAction(ari, channel, appName)
            }
            ActionEnum.PLAYBACK -> {
                ari.channels().play(channel.id, action.args.first()).execute().let { playback ->
                    channelStateCache.setTopAction(channel.id, action.copy(actionId = playback.id))
                }
            }
            ActionEnum.DIAL_TRUNK -> {
                val trunkName = action.args[0]
                ari.channels().setChannelVar(channel.id, "CALLERID(num)").setValue(channelState.peerDDR).execute()
                val channelB = ari.channels()
                    .create("PJSIP/103#${channel.dialplan.exten}@${trunkName}", appName)
                    .setAppArgs("${ActionEnum.DIAL_TRUNK.name},${channel.id},${channelState.peerDDR}")
                    .setOriginator(channel.id)
                    .setVariables(mapOf("PJSIP_HEADER(add,P-Asserted-Identity)" to channelState.controlNumber))
                    .execute()
                channelStateCache.addChannelState(ChannelState(
                    controlNumber = channelState.controlNumber,
                    channel = channelB,
                    channelLegEnum = ChannelLegEnum.B,
                    connectedChannel = channel.id,
                ))
            }
        }
    }

    fun dialTrunkHandler(ari: ARI, stasisStart: StasisStart) {
        logger.warn(stasisStart.args.toString())
        val channelAId = stasisStart.args[1]
        val channelB = stasisStart.channel
        val bridge = ari.bridges().create().setType("mixing").execute()
        ari.bridges().addChannel(bridge.id, channelAId).execute()
        ari.bridges().addChannel(bridge.id, channelB.id).execute()
        channelStateCache.updateChannelStateAttrs(channelAId, bridge.id, channelB.id)
        logger.info("${channelB.id} >> Discando com DDR: ${channelB.connected.number} para ${channelB.dialplan.exten}")
        ari.channels()
            .dial(channelB.id)
            .setTimeout(30)
            .execute()
    }

}