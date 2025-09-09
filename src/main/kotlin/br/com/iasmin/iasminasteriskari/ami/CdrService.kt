package br.com.iasmin.iasminasteriskari.ami

import org.asteriskjava.manager.event.CdrEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */
@Service
open class CdrService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${iasmin.backend-api}")
    private lateinit var BACKEND_API: String

    @Value("\${audio.record}")
    private lateinit var AUDIO_RECORD: String


    fun newCdr(cdrEvent: CdrEvent) {
        if (cdrEvent.destination == "s" || cdrEvent.destination == "*12345" || cdrEvent.destination.length < 5) return
        if (cdrEvent.billableSeconds > 0){
            val cdr = Cdr(cdrEvent)
            convertAudioToMp3(cdr)
            sendCdrToBackend(cdr)
        }
        // TODO: tratar CDRs de entrada adicionando peer = assistant-${cdr.company}
        //TODO: sendCdrToBackend(cdr)
    }
    
    private fun sendCdrToBackend(cdr: Cdr) {
        RestTemplate().postForObject("$BACKEND_API/cdr", cdr, Void::class.java)
    }

    internal fun convertAudioToMp3(cdr: Cdr){
        logger.info("${cdr.uniqueId} >> Convertendo audio para mp3 ${cdr.callRecord}")
        val mp3Dir = "$AUDIO_RECORD/mp3s"
        val audioFilePath = "$AUDIO_RECORD/${cdr.uniqueId.replace('.', '-')}-MIXED.sln"
        val mp3FilePath = "$mp3Dir/${cdr.callRecord}"
        val command = listOf("ffmpeg", "-i", audioFilePath, "-vn", "-acodec", "libmp3lame", "-ab", "128k", mp3FilePath)
        launch(command)
    }

    protected fun launch(command: List<String>) {
        ProcessBuilder(command).start()
    }

}

