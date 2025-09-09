package br.com.iasmin.iasminasteriskari.ari.channel

import br.com.iasmin.iasminasteriskari.ari.actions.AriAction
import ch.loway.oss.ari4java.generated.models.Channel

data class ChannelState(
    val controlNumber: String,
    val channel: Channel,
    val actions: MutableList<AriAction> = mutableListOf(),
    val channelLegEnum: ChannelLegEnum = ChannelLegEnum.A,
    val peerDDR: String? = null,
    val bridgeId: String? = null,
    val connectedChannel: String? = null
)
