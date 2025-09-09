package br.com.iasmin.iasminasteriskari

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class IasminAsteriskAriApplication

fun main(args: Array<String>) {
    runApplication<br.com.iasmin.iasminasteriskari.IasminAsteriskAriApplication>(*args)
}
