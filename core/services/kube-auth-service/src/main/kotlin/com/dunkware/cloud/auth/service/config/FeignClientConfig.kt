package com.dunkware.cloud.auth.service.config

import feign.Contract
import feign.RequestInterceptor
import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import feign.Logger
import org.springframework.cloud.openfeign.support.SpringMvcContract

@Configuration
class FeignClientConfig {
    @Bean
    fun feignLoggerLevel(): feign.Logger.Level {
        return feign.Logger.Level.FULL
    }

    @Bean
    fun feignContract(): Contract {
        return SpringMvcContract()
    }
}