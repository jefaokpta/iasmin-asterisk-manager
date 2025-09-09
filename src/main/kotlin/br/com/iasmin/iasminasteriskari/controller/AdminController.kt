package br.com.iasmin.iasminasteriskari.controller

import br.com.iasmin.iasminasteriskari.ari.channel.ChannelStateCache
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController(private val channelStateCache: ChannelStateCache) {

    @GetMapping("/channels")
    fun getAll() = channelStateCache.getAllChannelStates()

}