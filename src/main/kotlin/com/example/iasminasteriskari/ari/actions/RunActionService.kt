package com.example.iasminasteriskari.ari.actions

import ch.loway.oss.ari4java.ARI
import ch.loway.oss.ari4java.generated.models.Channel
import com.example.iasminasteriskari.ari.ChannelState
import com.example.iasminasteriskari.ari.ChannelStateCache
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RunActionService(private val channelStateCache: ChannelStateCache) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun runAction(ari: ARI, channel: Channel, appName: String) {
        channelStateCache.nextAction(channel.id)?.let { channelState ->
            handleAction(ari, channel, channelState, appName)
        }
    }

    private fun handleAction(ari: ARI, channel: Channel, channelState: ChannelState, appName: String) {
        val action = channelState.actions.removeFirstOrNull()
        when (action?.type) {
            ActionEnum.ANSWER -> ari.channels().answer(channel.id).execute()
            ActionEnum.HANGUP -> ari.channels().hangup(channel.id).execute()
            ActionEnum.PLAYBACK -> {
                ari.channels().play(channel.id, action.args.first()).execute().let {
                    playback -> channelStateCache.setTopAction(channel.id, action.copy(actionId = playback.id))
                }
            }
            ActionEnum.SET_VARIABLE -> {
                ari.channels().setChannelVar(channel.id, action.args[0]).setValue(action.args[1]).execute()
            }
            ActionEnum.SET_CDR_VARIABLE -> {
                ari.channels().setChannelVar(channel.id, action.args[0]).setValue(action.args[1]).execute()
                runAction(ari, channel, appName)
            }
            ActionEnum.DIAL_TRUNK -> {
                ari.channels()
                    .originate("PJSIP/103#${channel.dialplan.exten}@${action.args[0]}")
                    .setApp(appName)
                    .setCallerId(action.args[1])
                    .setVariables(mapOf("PJSIP_HEADER(add,P-Asserted-Identity)" to action.args[1]))
                    .setTimeout(30)
                    .setAppArgs("dialed")
                    .execute()
            }

            else -> {
                logger.warn("${channel.id} >> Ação desconhecida ou lista vazia")
                ari.channels().hangup(channel.id).execute()
            }
        }
    }
}