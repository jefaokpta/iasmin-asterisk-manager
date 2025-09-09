package br.com.iasmin.iasminasteriskari.ami

import org.asteriskjava.manager.ManagerEventListener
import org.asteriskjava.manager.event.*
import org.springframework.stereotype.Service

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 09/09/2025
 */

@Service
class AmiEventHandler(): ManagerEventListener {

    override fun onManagerEvent(event: ManagerEvent) {
//        println(event)
        when(event) {
            is CdrEvent -> println(event.toJson())
//            is InvalidAccountId -> this.blockIps(event.remoteAddress.split("/")[2])
//            is InvalidPasswordEvent -> this.blockIps(event.remoteAddress.split("/")[2])
        }
    }

    /**
    if (event is VarSetEvent) return
    if (event is ExtensionStatusEvent) return
    if (event is NewExtenEvent) return
    if (event is QueueSummaryCompleteEvent) return
    if (event is QueueStatusCompleteEvent) return
    if (event is RtcpSentEvent) return
    if (event is RtcpReceivedEvent) return
    if (event is ChannelReloadEvent) return
    if (event is NewCallerIdEvent) return
    if (event is BridgeEvent) {
    //HandleEvent().handle(event as BridgeEvent?)
    return
    }
    if (event is NewAccountCodeEvent) return
    if (event is ChannelUpdateEvent) return
    if (event is JitterBufStatsEvent) return
    if (event is DialEvent) {
    //HandleEvent().handle(event as DialEvent?)
    return
    }
    if (event is MusicOnHoldEvent) return
    if (event is RenameEvent) return
    if (event is MasqueradeEvent) return
    if (event is DtmfEvent) return
    if (event is JoinEvent) return
    if (event is QueueMemberAddedEvent) return
    if (event is RegistryEvent) return
    if (event is SoftHangupRequestEvent) return
    if (event is HangupRequestEvent) return
    if (event is HoldEvent) return
    if (event is UnholdEvent) return
    if (event is AgentCompleteEvent) return
    if (event is AgentRingNoAnswerEvent) return
    if (event is DisconnectEvent) return
    if (event is FullyBootedEvent) return
    if (event is ConnectEvent) return
    if (event is MessageWaitingEvent) return
    if (event is MeetMeJoinEvent) return
    if (event is MeetMeEndEvent) return
    if (event is MeetMeLeaveEvent) return
    if (event is ConfbridgeEndEvent) return
    if (event is ConfbridgeJoinEvent) return
    if (event is ConfbridgeLeaveEvent) return

    // ASTERISK_JAVA 3.6
    if (event is SuccessfulAuthEvent) return
    if (event is ChallengeSentEvent) return
    if (event is DeviceStateChangeEvent) return
    if (event is InvalidPasswordEvent) return
    if (event is NewConnectedLineEvent) return
    if (event is BridgeEnterEvent) return
    if (event is QueueCallerLeaveEvent) return
    if (event is BridgeCreateEvent) return
    if (event is BridgeDestroyEvent) return
    if (event is BridgeLeaveEvent) return
    if (event is LocalBridgeEvent) return
    if (event is QueueCallerJoinEvent) return
    if (event is LocalOptimizationEndEvent) return
    if (event is BlindTransferEvent) return
    if (event is AttendedTransferEvent) return
    if (event is BridgeMergeEvent) return
    if (event is LocalOptimizationBeginEvent) return

    println(event)
     **/
}