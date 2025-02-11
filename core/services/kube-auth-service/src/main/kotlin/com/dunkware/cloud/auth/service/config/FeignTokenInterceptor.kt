package com.dunkware.cloud.auth.service.config

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.security.core.Authentication


import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class FeignTokenInterceptor : RequestInterceptor {
    override fun apply(template: RequestTemplate) {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null) {
            template.header("Authorization", "Bearer ${getToken(authentication)}")
        }
    }

     fun getToken(authentication: Authentication): String {
        return when {
            authentication.credentials is String -> authentication.credentials as String
            authentication.principal is UserDetails -> (authentication.principal as UserDetails).username
            else -> throw AuthenticationException("Invalid authentication principal")
        }
    }
}

class AuthenticationException(message: String) : RuntimeException(message)
