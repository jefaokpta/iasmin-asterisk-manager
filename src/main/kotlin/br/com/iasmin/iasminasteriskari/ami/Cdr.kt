package br.com.iasmin.iasminasteriskari.ami

import org.asteriskjava.manager.event.CdrEvent

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */
data class Cdr(
    val peer: String,
    val src: String,
    val destination: String,
    val callerId: String,
    val duration: Int,
    val billableSeconds: Int,
    val uniqueId: String,
    val disposition: String,
    val company: String,
    val startTime: String,
    val callRecord: String?,
    val channel: String,
    val userfield: String,
    val destinationChannel: String,
) {
    
    constructor(cdrEvent: CdrEvent) : this(
        peer = cdrEvent.dynamicProperties["peer"] as String,
        src = cdrEvent.src,
        destination = cdrEvent.destination,
        callerId = cdrEvent.callerId,
        duration = cdrEvent.duration,
        billableSeconds = cdrEvent.billableSeconds,
        uniqueId = cdrEvent.uniqueId,
        disposition = cdrEvent.disposition,
        company = cdrEvent.dynamicProperties["company"] as String,
        startTime = cdrEvent.startTime,
        callRecord = if (cdrEvent.billableSeconds > 0) "${cdrEvent.uniqueId.replace(".","-")}.mp3" else null,
        channel = cdrEvent.channel,
        userfield = cdrEvent.userField,
        destinationChannel = cdrEvent.destinationChannel
    )

}

/*
* 400 Bad Request on POST request for "https://iasmin-backend-app-7qq6g.ondigitalocean.app/cdr": "{"message":["callerId should not be empty","userfield must be one of the following values: OUTBOUND, INBOUND, UPLOAD, TEST"],"error":"Bad Request","statusCode":400}"
*
* org.asteriskjava.manager.event.CdrEvent[
* dateReceived='Tue Sep 09 12:26:39 BRT 2025',privilege='cdr,all',server=null,calleridname=null,starttimeasdate='Tue Sep 09 12:26:37 BRT 2025',destination='1131151515',channel='PJSIP/1-00000123',lastdata='outbound-router-call-app',starttime='2025-09-09 12:26:37',destinationcontext='VIP-PEERS',exten=null,duration='2',userfield='OUTBOUND',calleridnum=null,recordfile=null,context=null,callerid='"Jefao" <1>',amaflags='DOCUMENTATION',connectedlinenum=null,uniqueid='1757431597.1004',timestamp=null,channelstatedesc=null,lastapplication='Stasis',src='1',systemname=null,billableseconds='0',endtime='2025-09-09 12:26:39',connectedlinename=null,priority=null,sequencenumber=null,channelstate=null,
* dynamicproperties='{linkedid=1757431597.1004, dstfinal=, callrecord=, peer=1, company=100023}',
* disposition='NO ANSWER',destinationchannel='PJSIP/IASMIN_JUPITER-00000124',answertime='',answertimeasdate=null,endtimeasdate='Tue Sep 09 12:26:39 BRT 2025',accountcode='',systemHashcode=306335967]

* */