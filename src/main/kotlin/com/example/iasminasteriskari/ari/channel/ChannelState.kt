package com.example.iasminasteriskari.ari.channel

import ch.loway.oss.ari4java.generated.models.Channel
import com.example.iasminasteriskari.ari.actions.AriAction

data class ChannelState(
    val controlNumber: String,
    val channel: Channel,
    val actions: MutableList<AriAction> = mutableListOf(),
    val channelLegEnum: ChannelLegEnum = ChannelLegEnum.A,
    val peerDDR: String? = null,
    val bridgeId: String? = null,
    val connectedChannel: String? = null
)
