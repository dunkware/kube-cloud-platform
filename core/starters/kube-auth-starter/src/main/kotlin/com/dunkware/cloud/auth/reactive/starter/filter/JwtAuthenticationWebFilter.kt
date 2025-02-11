package com.dunkware.cloud.auth.reactive.starter.filter;

import com.dunkware.cloud.auth.reactive.starter.config.JwtProperties
import com.dunkware.cloud.auth.reactive.starter.config.SecurityProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import javax.crypto.SecretKey


@Component
class JwtAuthenticationWebFilter(
    private val jwtProperties: SecurityProperties
) : WebFilter {

    private fun getSigningKey(): SecretKey {
        val minKeyLength = 64 // 512 bits minimum for HS512
        val secretKey = (System.getenv("JWT_SECRET_KEY") ?:
        "defaultSecretKeyForDevelopmentOnlyDoNotUseInProduction!!MustBeAtLeast64BytesLongForHS512Algorithm!#@123456789")
            .padEnd(minKeyLength, '!') // Ensure minimum length by padding if needed
            .toByteArray()

        return Keys.hmacShaKeyFor(secretKey)
    }

    // Replace the direct signingKey initialization with the function
    private val signingKey = getSigningKey()
    private val jwtParser = Jwts.parser().verifyWith(signingKey).build()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return extractToken(exchange.request)
            .doOnNext { token ->
                logger.debug("Extracted token: ${token != null}")
            }
            .filter { token ->
                val isValid = validateToken(token)
                logger.debug("Token validation result: $isValid")
                isValid
            }
            .map { token ->
                logger.debug("Extracting claims from token")
                extractClaims(token)
            }
            .map { claims ->
                logger.debug("Creating authentication from claims")
                createAuthentication(claims)
            }
            .flatMap { authentication ->
                logger.debug("Setting authentication in security context")
                chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
            }
            .onErrorResume { error ->
                logger.error("Error during authentication", error)
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                exchange.response.setComplete()
            }
            .doOnError { error ->
                logger.error("Error in filter chain", error)
            }
            .switchIfEmpty(
                Mono.defer {
                    logger.debug("No token found or invalid token, continuing chain without authentication")
                    chain.filter(exchange)
                }
            )
    }

    private fun extractToken(request: ServerHttpRequest): Mono<String> {
        return Mono.justOrEmpty(
            request.headers.getFirst(HttpHeaders.AUTHORIZATION)
                ?.takeIf { it.startsWith("Bearer ") }
                ?.substring(7)
        )
    }

    private fun validateToken(token: String): Boolean {
        return try {
            jwtParser.parseSignedClaims(token)
            true
        } catch (e: Exception) {
            logger.warn("JWT validation failed", e)
            false;
        }
    }

    private fun extractClaims(token: String): Claims {
        return jwtParser.parseSignedClaims(token).payload
    }

    private fun createAuthentication(claims: Claims): UsernamePasswordAuthenticationToken {
        val username = claims.subject
        val authorities = buildList {
            (claims["roles"] as? List<*>)?.forEach { role ->
                add(SimpleGrantedAuthority("ROLE_$role"))
            }
            (claims["permissions"] as? List<*>)?.forEach { permission ->
                add(SimpleGrantedAuthority(permission.toString()))
            }
        }

        return UsernamePasswordAuthenticationToken(
            username,
            null,
            authorities
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JwtAuthenticationWebFilter::class.java)
    }
}
