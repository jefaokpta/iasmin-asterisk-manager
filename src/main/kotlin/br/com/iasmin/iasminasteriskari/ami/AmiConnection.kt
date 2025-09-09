package br.com.iasmin.iasminasteriskari.ami

import jakarta.annotation.PostConstruct
import org.asteriskjava.manager.ManagerConnection
import org.asteriskjava.manager.ManagerConnectionFactory
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

    private var ami: ManagerConnection? = null

    @PostConstruct
    fun init() {
        logger.info("\uD83D\uDE80 Ami conectando...")
        connectManager()
    }

    private fun connectManager(): ManagerConnection {
        if (ami == null) {
            ami = ManagerConnectionFactory(
                host,
                user,
                password
            ).createManagerConnection().apply {
                addEventListener(amiEventHandler)
                login()
            }
        }
        return ami!!
    }

    fun ami() = connectManager()
}