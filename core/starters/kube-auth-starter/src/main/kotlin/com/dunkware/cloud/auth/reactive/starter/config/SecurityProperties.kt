package com.dunkware.cloud.auth.reactive.starter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties

@EnableConfigurationProperties(SecurityProperties::class)
@ConfigurationProperties(prefix = "dunkware.security")
data class SecurityProperties(
    val permitAll: List<String> = listOf("/actuator/*"),
    val cors: CorsProperties = CorsProperties(),
    val jwt: JwtProperties = JwtProperties()
)

data class CorsProperties(
    val allowedOrigins: List<String> = listOf("http://localhost:5174", "http://localhost:5173"),
    val allowedMethods: List<String> = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS"),
    val allowedHeaders: List<String> = listOf(""
    ),
    val exposedHeaders: List<String> = listOf("*"
    ),
    val allowCredentials: Boolean = true,
    val maxAge: Long = 3600L
)

data class JwtProperties(
    val secretKey: String = "defaultSecretKeyForDevelopmentOnlyDoNotUseInProduction!!"
)