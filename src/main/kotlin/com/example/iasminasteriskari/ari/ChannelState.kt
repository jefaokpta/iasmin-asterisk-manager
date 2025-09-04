package com.example.iasminasteriskari.ari

import ch.loway.oss.ari4java.generated.models.Channel
import com.example.iasminasteriskari.ari.actions.AriAction

data class ChannelState(
    val controlNumber: String,
    val peerDDR: String?,
    val channel: Channel,
    val actions: MutableList<AriAction>
)
