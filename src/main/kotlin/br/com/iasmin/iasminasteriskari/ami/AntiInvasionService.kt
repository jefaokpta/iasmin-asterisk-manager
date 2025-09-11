package br.com.iasmin.iasminasteriskari.ami

import org.asteriskjava.manager.event.InvalidAccountId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.time.LocalDateTime

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */
@Service
class AntiInvasionService {

    private val invaders = mutableMapOf<String, Invader>()
    private val blockedInvaders = mutableMapOf<String, Invader>()
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val BLOCKED_FILE = "/tmp/blocked_ips.bin"

    fun antiInvasion(invalidAccountId: InvalidAccountId) {
        val ip = invalidAccountId.remoteAddress.split("/")[2]
        if (invaders.containsKey(ip)) {
            blockOrSumInvader(invaders.getValue(ip))
            return
        }
        invaders[ip] = Invader(invalidAccountId)
    }

    private fun blockOrSumInvader(invader: Invader) {
        if (invader.attempts > 5) {
            if (LocalDateTime.now().minusMinutes(1).isBefore(invader.firstAttempt)) {
                blockedInvaders[invader.ip] = invader
                return
            }
            invaders.remove(invader.ip)
            return
        }
        invaders[invader.ip] = invader.copy(attempts = invader.attempts + 1)
    }

    fun blockedToFile() {
        if (File(BLOCKED_FILE).exists()) {
            val fis = FileInputStream(BLOCKED_FILE)
            val ois = ObjectInputStream(fis)
            val blockedIps: Set<String> = ois.readObject() as Set<String>
            if (blockedIps == blockedInvaders.keys.toSet()) {
                logger.info("Nenhum IP novo para bloquear, não vou salvar")
                return
            }
        }
        logger.info("Bloqueando novos IPs")
        val fos = FileOutputStream(BLOCKED_FILE)
        val oos = ObjectOutputStream(fos)
        val blockedIps = blockedInvaders.keys.toSet()
        oos.writeObject(blockedIps)
        oos.close()
        fos.close()
        ProcessBuilder("iptables", "-F", "INPUT").start()
        blockedIps.forEach {
            ProcessBuilder("iptables", "-I", "INPUT", "-s", it, "-j", "DROP").start()
        }
        logger.info("IPs bloqueados $blockedIps")
    }

    fun clearAll() {
        logger.info("Limpando todos os IPs bloqueados")
        blockedInvaders.clear()
        invaders.clear()
    }
}
