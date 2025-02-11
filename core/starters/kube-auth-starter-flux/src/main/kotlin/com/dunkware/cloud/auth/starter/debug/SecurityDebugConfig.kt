package com.dunkware.cloud.auth.starter.debug

import jakarta.servlet.Filter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.context.SecurityContextHolder

@Configuration
class SecurityDebugConfig {

    @Bean
    fun debugFilter(): Filter {
        return Filter { request, response, chain ->
            val httpRequest = request as HttpServletRequest
            println("⭐ Request: ${httpRequest.method} ${httpRequest.requestURI}")
            println("⭐ Authentication: ${SecurityContextHolder.getContext().authentication}")
            println("⭐ Authorities: ${SecurityContextHolder.getContext().authentication?.authorities}")
            chain.doFilter(request, response)
            println("⭐ Response status: ${(response as HttpServletResponse).status}")
        }
    }
}