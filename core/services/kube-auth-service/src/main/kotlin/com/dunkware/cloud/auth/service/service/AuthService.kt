package com.dunkware.cloud.auth.service.service

import com.dunkware.cloud.user.model.AuthRequest
import com.dunkware.cloud.user.model.TokenResponse
import com.dunkware.cloud.auth.service.client.CredentialsValidationRequest
import com.dunkware.cloud.auth.service.client.UserServiceClient
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userServiceClient: UserServiceClient,
    private val jwtService: JwtService
) {
    fun authenticate(request: AuthRequest): TokenResponse {
        val userDetails = userServiceClient.validateCredentials(
            CredentialsValidationRequest(
                username = request.username,
                password = request.password
            )
        )
        
        val token = jwtService.generateToken(userDetails)
        
        return TokenResponse(token)
    }
}
