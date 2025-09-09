package br.com.iasmin.iasminasteriskari

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor


@Configuration
class ThreadPoolConfig {
    @Bean
    fun ariTaskExecutor(): ThreadPoolTaskExecutor {
        val pool = ThreadPoolTaskExecutor()
        pool.corePoolSize = 32
        pool.maxPoolSize = 64
        pool.setThreadNamePrefix("ARI-")
        return pool
    }
}