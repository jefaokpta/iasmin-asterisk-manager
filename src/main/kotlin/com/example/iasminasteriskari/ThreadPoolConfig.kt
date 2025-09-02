package com.example.iasminasteriskari

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor


@Configuration
class ThreadPoolConfig {
    @Bean
    fun ariTaskExecutor(): ThreadPoolTaskExecutor {
        val pool = ThreadPoolTaskExecutor()
        pool.setCorePoolSize(5)
        pool.setMaxPoolSize(10)
        pool.setThreadNamePrefix("ARI-")
        return pool
    }
}