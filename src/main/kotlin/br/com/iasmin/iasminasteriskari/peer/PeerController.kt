package br.com.iasmin.iasminasteriskari.peer

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */
@RestController
@RequestMapping("/peers")
class PeerController(private val peerService: PeerService) {

    @PostMapping
    fun writePeer(@RequestBody users: List<User>) = peerService.writePeer(users)

}