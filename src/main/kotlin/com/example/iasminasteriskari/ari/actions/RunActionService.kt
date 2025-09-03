package com.example.iasminasteriskari.ari.actions

import ch.loway.oss.ari4java.ARI
import ch.loway.oss.ari4java.generated.models.Channel
import ch.loway.oss.ari4java.generated.models.Playback
import ch.loway.oss.ari4java.tools.AriCallback
import ch.loway.oss.ari4java.tools.RestException
import com.example.iasminasteriskari.ari.ChannelStateCache
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RunActionService(private val channelStateCache: ChannelStateCache) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun runAction(ari: ARI, channel: Channel) {
        val action = channelStateCache.nextAction(channel.id) ?: return
        handleAction(ari, channel, action)
    }

    private fun handleAction(ari: ARI, channel: Channel, action: AriAction) {
        when (action.action) {
            ActionEnum.ANSWER -> ari.channels().answer(channel.id).execute()
            ActionEnum.HANGUP -> ari.channels().hangup(channel.id).execute()
            ActionEnum.PLAYBACK -> ari.channels().play(channel.id, action.args.first()).execute(object: AriCallback<Playback> {
                override fun onSuccess(result: Playback?) {
                    logger.warn("${channel.id} Playback terminou")
                    runAction(ari, channel)
                }

                override fun onFailure(e: RestException?) {
                    logger.error("${channel.id} >> Playback falhou", e)
                    runAction(ari, channel)
                }
            })
        }
    }
}