package com.dunkware.cloud.auth.service.controller

import com.dunkware.cloud.auth.service.client.CredentialsValidationRequest
import com.dunkware.cloud.auth.service.client.UserServiceClient
import com.dunkware.cloud.auth.service.config.AuthenticationException
import com.dunkware.cloud.user.model.AuthRequest
import com.dunkware.cloud.user.model.TokenResponse
import com.dunkware.cloud.user.model.TokenValidationRequest
import com.dunkware.cloud.user.model.TokenValidationResponse
import com.dunkware.cloud.auth.service.service.JwtService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController

class AuthController(
    private val jwtService: JwtService,
    private val userServiceClient: UserServiceClient
) {

    @PostMapping("/login")
    fun getToken(@RequestBody request: AuthRequest): TokenResponse {

        try {
            // Convert AuthRequest to CredentialsValidationRequest
            val credentials = CredentialsValidationRequest(
                username = request.username,
                password = request.password
            )

            val userDetails = userServiceClient.validateCredentials(credentials)

            val token = jwtService.generateToken(userDetails)
            return TokenResponse(token)
        } catch (e: Exception) {
            throw AuthenticationException("Authentication failed " + e.to(String));
        }
    }

    @PostMapping("/validate")
    fun validateToken(@RequestBody request: TokenValidationRequest): TokenValidationResponse {
        val isValid = jwtService.validateToken(request.token)
        return TokenValidationResponse(isValid)
    }
}
