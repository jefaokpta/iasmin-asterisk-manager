package br.com.iasmin.iasminasteriskari.cron

import br.com.iasmin.iasminasteriskari.ami.AntiInvasionService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */
@Component
class Task(private val antiInvasionService: AntiInvasionService) {

    // Executa a cada 5 minutos no segundo 0
    @Scheduled(cron = "0 */5 * * * *")
    fun runBlockedToFile() {
        antiInvasionService.blockedToFile()
    }

    // As 03:00 libera todos os IPs bloqueados
    @Scheduled(cron = "0 0 3 * * *")
    fun releaseBlockedIps() {
        antiInvasionService.clearAll()
    }
}