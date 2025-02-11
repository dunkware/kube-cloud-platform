package com.dunkware.cloud.auth.reactive.starter.config

import com.dunkware.cloud.auth.reactive.starter.filter.JwtAuthenticationWebFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity(securedEnabled = true)
@EnableConfigurationProperties(SecurityProperties::class)
class SecurityConfig(
    private val jwtAuthenticationWebFilter: JwtAuthenticationWebFilter,
    private val securityProperties: SecurityProperties
) {


    @Bean
    fun corsWebFilter(): CorsWebFilter {
        return CorsWebFilter(corsConfiguration())
    }


    @Bean
    fun corsConfiguration(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            exposedHeaders = listOf("*")
            allowedHeaders = listOf("*")
            allowedMethods = listOf("*")
            allowedOriginPatterns = listOf("*")
            allowedOrigins = listOf("http://localhost:5173")
            maxAge = 30L
            allowCredentials = true
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }
}

@Bean
fun securityWebFilterChain(http: ServerHttpSecurity, securityProperties: SecurityProperties, filter: JwtAuthenticationWebFilter): SecurityWebFilterChain {
    return http

        .csrf {
            it.disable()
        }
        .authorizeExchange { auth ->
            auth
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers("/api/auth/v1/login", "/api/auth/v1/validate").permitAll()
                .anyExchange().authenticated()
        }

        .cors { it.disable() }
        .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)

        .build()
}


