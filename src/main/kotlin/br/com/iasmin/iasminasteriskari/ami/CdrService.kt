package br.com.iasmin.iasminasteriskari.ami

import org.asteriskjava.manager.event.CdrEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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

    private val TIMEOUT = Duration.ofSeconds(10)


    fun newCdr(cdrEvent: CdrEvent) {
        if (cdrEvent.destination == "s" || cdrEvent.destination == "*12345" || cdrEvent.destination.length < 5) return
        val cdr = Cdr(cdrEvent)
        val task = Executors.newVirtualThreadPerTaskExecutor()
        task.submit {
            TimeUnit.SECONDS.sleep(1)
            if (cdrEvent.billableSeconds > 0) {
                convertAudioToMp3(cdr)
                sendCdrToBackend(cdr)
                return@submit
            }
            // TODO: tratar CDRs de entrada adicionando peer = assistant-${cdr.company}
            sendCdrToBackend(cdr)
        }
        task.shutdown()
    }
    
    private fun sendCdrToBackend(cdr: Cdr) {
        val restTemplate = RestTemplate()
        restTemplate.requestFactory = SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(TIMEOUT)
        }
        restTemplate.postForObject("$BACKEND_API/cdr", cdr, Void::class.java)
        logger.info("${cdr.uniqueId} >> Enviado CDR para backend")
    }

    internal fun convertAudioToMp3(cdr: Cdr){
        logger.info("${cdr.uniqueId} >> Convertendo audio para mp3 ${cdr.callRecord}")
        val mp3Dir = "$AUDIO_RECORD/mp3s"
        val audioFilePath = "$AUDIO_RECORD/${cdr.uniqueId.replace('.', '-')}-mixed.sln"
        val mp3FilePath = "$mp3Dir/${cdr.callRecord}"
        val command = listOf("ffmpeg", "-i", audioFilePath, "-vn", "-acodec", "libmp3lame", "-ab", "128k", mp3FilePath)
        launch(command)
    }

    protected fun launch(command: List<String>) {
        ProcessBuilder(command).start()
    }

}

