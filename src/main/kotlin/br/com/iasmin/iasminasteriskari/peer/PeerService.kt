package br.com.iasmin.iasminasteriskari.peer

import br.com.iasmin.iasminasteriskari.ami.AmiConnection
import org.asteriskjava.manager.action.CommandAction
import org.springframework.stereotype.Service

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */
@Service
class PeerService(
    private val peerWriterService: PeerWriterService,
    private val amiConnection: AmiConnection
) {

    fun writePeer(users: List<User>) {
        peerWriterService.writePeers(users)
        amiConnection.sendActionAsync(CommandAction("pjsip reload"))
    }
}