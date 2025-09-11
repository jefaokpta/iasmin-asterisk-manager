package br.com.iasmin.iasminasteriskari.ami

import jakarta.annotation.PostConstruct
import org.asteriskjava.manager.ManagerConnection
import org.asteriskjava.manager.ManagerConnectionFactory
import org.asteriskjava.manager.action.GetVarAction
import org.asteriskjava.manager.action.ManagerAction
import org.asteriskjava.manager.response.ManagerResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class AmiConnection(private val amiEventHandler: AmiEventHandler) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    @Value("\${ami.host}")
    private var host: String = ""

    @Value("\${ami.user}")
    private var user: String = ""

    @Value("\${ami.password}")
    private var password: String = ""

    private var managerConnection: ManagerConnection? = null

    @PostConstruct
    fun init() {
        logger.info("\uD83D\uDE80 Ami conectando...")
        connectManager()
    }

    private fun connectManager(): ManagerConnection {
        if (managerConnection == null) {
            managerConnection = ManagerConnectionFactory(
                host,
                user,
                password
            ).createManagerConnection().apply {
                addEventListener(amiEventHandler)
                login()
            }
        }
        return managerConnection!!
    }

    fun sendActionAsync(action: ManagerAction) {
        val ami = managerConnection ?: connectManager()
        ami.sendAction(action){
            println("AMI ASYNC ACTION RETORNO: $it")
        }
    }

    fun getVariableAsync(channel: String, variable: String, callback: (ManagerResponse) -> Unit) {
        val ami = managerConnection ?: connectManager()
        ami.sendAction(GetVarAction(channel, variable)){
            callback(it)
        }
    }
}