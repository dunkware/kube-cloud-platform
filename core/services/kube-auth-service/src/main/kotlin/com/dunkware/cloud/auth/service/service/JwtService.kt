package com.dunkware.cloud.auth.service.service



import com.dunkware.cloud.user.domain.UserDetailsDto
import com.dunkware.cloud.user.model.response.UserResponse
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService {
    private val secretKey = getSigningKey()

    fun generateToken(details: UserResponse): String {
        return Jwts.builder()
            .subject(details.username)
            .claim("roles", details.roles)
            .claim("permissions", details.permissions)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + TOKEN_VALIDITY))
            .claim("firstName", details.firstName)
            .claim("lastName", details.lastName)
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(secretKey)  // Use the same key instance
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getSigningKey(): SecretKey {
        val minKeyLength = 64 // 512 bits minimum for HS512
        val secretKey = (System.getenv("JWT_SECRET_KEY") ?:
        "defaultSecretKeyForDevelopmentOnlyDoNotUseInProduction!!MustBeAtLeast64BytesLongForHS512Algorithm!#@123456789")
            .padEnd(minKeyLength, '!') // Ensure minimum length by padding if needed
            .toByteArray()

        return Keys.hmacShaKeyFor(secretKey)
    }
    companion object {
        private const val TOKEN_VALIDITY = 24 * 60 * 60 * 1000L // 24 hours
    }
}