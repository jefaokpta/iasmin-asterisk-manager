package br.com.iasmin.iasminasteriskari.ami

import org.asteriskjava.manager.event.InvalidAccountId
import java.time.LocalDateTime

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */
data class Invader(
    val ip: String,
    val attempts: Int,
    val firstAttempt: LocalDateTime
) {

    constructor(invalidAccountId: InvalidAccountId) : this(
        ip = invalidAccountId.remoteAddress.split("/")[2],
        attempts = 0,
        firstAttempt = LocalDateTime.now()
    )
}