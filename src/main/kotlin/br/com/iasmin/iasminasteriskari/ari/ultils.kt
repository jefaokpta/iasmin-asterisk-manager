package br.com.iasmin.iasminasteriskari.ari

import br.com.iasmin.iasminasteriskari.ari.channel.ChannelLegEnum
import ch.loway.oss.ari4java.ARI

fun createRecordName(channelId: String, channelLegEnum: ChannelLegEnum): String {
    return "${channelId.replace(".", "-")}-${channelLegEnum.name.lowercase()}"
}

fun recordBridge(ari: ARI, bridgeId: String, channelId: String){
    ari.bridges().record(bridgeId, createRecordName(channelId, ChannelLegEnum.MIXED), "sln").execute()
}

fun createSnoopChannelToRecord(ari: ARI, channelId: String, appName: String, recordName: String){
    ari.channels().snoopChannel(channelId, appName)
        .setAppArgs("record,$recordName")
        .setSpy("in")
        .setWhisper("none")
        .execute()
}

fun recordChannel(ari: ARI, channelId: String, recordName: String){
    ari.channels().record(channelId, recordName, "sln").execute()
}