package br.com.iasmin.iasminasteriskari.peer

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */
@RestController
@RequestMapping("/peers")
class PeerController {

    @PostMapping
    fun writePeer(): String {
        return "Peer created"
    }

}