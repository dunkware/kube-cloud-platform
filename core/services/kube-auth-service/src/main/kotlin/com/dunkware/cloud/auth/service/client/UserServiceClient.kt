package com.dunkware.cloud.auth.service.client


import com.dunkware.cloud.auth.service.config.FeignClientConfig
import com.dunkware.cloud.user.domain.UserDetailsDto
import com.dunkware.cloud.user.model.response.UserResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "cr-user-service",
    configuration = [FeignClientConfig::class]
)
interface UserServiceClient {
    @PostMapping("/users/validate")
    fun validateCredentials(@RequestBody request: CredentialsValidationRequest): UserResponse
}

data class CredentialsValidationRequest(
    val username: String,
    val password: String
)
