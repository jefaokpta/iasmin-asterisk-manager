package br.com.iasmin.iasminasteriskari.peer

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */
class User(
    val id: Int,
    val name: String,
    val controlNumber: String,
    val roles: List<String>,
    val ddr: String,
) {
}