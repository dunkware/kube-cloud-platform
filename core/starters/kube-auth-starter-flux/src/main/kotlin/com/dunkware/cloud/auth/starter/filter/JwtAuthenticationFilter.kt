package com.dunkware.cloud.auth.starter.filter


import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.crypto.SecretKey


class JwtAuthenticationFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = extractToken(request)
            if (token != null && validateToken(token)) {
                val claims = extractClaims(token)
                setAuthentication(claims)
            }
        } catch (e: Exception) {
            logger.error("Could not set user authentication in security context", e)
        }

        filterChain.doFilter(request, response)
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken?.startsWith("Bearer ") == true) {
            return bearerToken.substring(7)
        }
        return null
    }

    private fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun extractClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    private fun setAuthentication(claims: Claims) {
        val username = claims.subject
        val roles = (claims["roles"] as List<*>).map { "ROLE_$it" }
        val permissions = claims["permissions"] as List<*>

        // Convert both roles and permissions to authorities
        val authorities = (roles + permissions).map { SimpleGrantedAuthority(it.toString()) }

        val authentication = UsernamePasswordAuthenticationToken(
            username,
            null,
            authorities
        )

        SecurityContextHolder.getContext().authentication = authentication
    }

    private fun getSigningKey(): SecretKey {
        val minKeyLength = 64 // 512 bits minimum for HS512
        val secretKey = (System.getenv("JWT_SECRET_KEY") ?:
        "defaultSecretKeyForDevelopmentOnlyDoNotUseInProduction!!MustBeAtLeast64BytesLongForHS512Algorithm!#@123456789")
            .padEnd(minKeyLength, '!') // Ensure minimum length by padding if needed
            .toByteArray()

        return Keys.hmacShaKeyFor(secretKey)
    }


}