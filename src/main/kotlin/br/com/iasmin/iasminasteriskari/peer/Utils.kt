package br.com.iasmin.iasminasteriskari.peer

import java.security.MessageDigest

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */

fun generateMd5Hash(user: User): String {
    val md = MessageDigest.getInstance("MD5")
    val digested = md.digest("${user.id}:asterisk:IASMIN_WEBPHONE_${user.id}".toByteArray())
    return digested.joinToString("") { "%02x".format(it) }
}