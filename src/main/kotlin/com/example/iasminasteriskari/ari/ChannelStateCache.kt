package com.example.iasminasteriskari.ari

import ch.loway.oss.ari4java.generated.models.Channel
import com.example.iasminasteriskari.ari.actions.AriAction
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ChannelStateCache {

    private val channelStates = mutableMapOf<String, ChannelState>()
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Synchronized
    fun addChannelState(channelState: ChannelState) {
        channelStates[channelState.channel.id] = channelState
    }

    @Synchronized
    fun updateChannel(channel: Channel) {
        val channelState = channelStates[channel.id] ?: return
        channelStates[channel.id] = channelState.copy(channel = channel)
    }

    @Synchronized
    fun updateChannelStateAttrs(channelId: String, bridgeId: String, channelBId: String?) {
        channelStates[channelId] = channelStates[channelId]?.copy(bridgeId = bridgeId, channelBId = channelBId) ?: return
    }

    @Synchronized
    fun getChannelState(channelId: String): ChannelState? {
        return channelStates[channelId]
    }

    @Synchronized
    fun removeChannelState(channel: Channel) = channelStates.remove(channel.id)

    @Synchronized
    fun removeActionByActionId(actionId: String): ChannelState? {
        val channelState = channelStates.values.find { it.actions.any { action -> action.actionId == actionId } }
        channelState?.actions?.removeFirstOrNull()
        return channelState
    }

    @Synchronized
    fun setTopAction(channelId: String, action: AriAction) {
        channelStates[channelId]?.actions?.addFirst(action)
    }

}