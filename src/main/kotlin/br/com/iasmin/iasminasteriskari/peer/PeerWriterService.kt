package br.com.iasmin.iasminasteriskari.peer

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.FileWriter

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */
@Service
class PeerWriterService {

    @Value("\${asterisk.config}")
    private lateinit var ASTERISK_FOLDER: String

    fun writePeers(users: List<User>){
        val writer = FileWriter("$ASTERISK_FOLDER/pjsip-peers.conf")
        writer.write(";Arquivo escrito automaticamente pelo sistema\n\n")
        users.forEach { user ->
            println("Writing peer: ${user.id}")
            writer.write(pjsipPeer(user))
        }
        writer.flush()
        writer.close()
    }

    private fun pjsipPeer(user: User): String {
        return """
;=============== ENDPOINT: ${user.id}
[${user.id}]
type=endpoint
transport=transport-wss
webrtc=yes
context=VIP-PEERS
callerid=${user.name} <${user.id}>
language=pt_BR
named_call_group=${user.controlNumber}
named_pickup_group=${user.controlNumber}
dtmf_mode=rfc4733
disallow=all
allow=alaw
auth=${user.id}
aors=${user.id}
set_var=CDR(company)=${user.controlNumber}
set_var=CDR(peer)=${user.id}
set_var=PEER_DDR=${user.ddr}
set_var=CALL_LIMIT=2
set_var=GROUP()=${user.id}
set_var=__TRANSFER_CONTEXT=TRANSFERING

;=============== AORS: ${user.id}
[${user.id}]
type=aor
qualify_frequency=0
max_contacts=2

;=============== AUTH: ${user.id}
[${user.id}]
type=auth
auth_type=md5
username=${user.id}
md5_cred=${generateMd5Hash(user)}
;=============== FIM: ${user.id}
        """.trimIndent()
    }

}