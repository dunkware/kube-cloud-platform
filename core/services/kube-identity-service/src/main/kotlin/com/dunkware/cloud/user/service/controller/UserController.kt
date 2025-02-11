package com.dunkware.cloud.user.service.controller


import com.dunkware.cloud.user.model.request.CreateUserRequest
import com.dunkware.cloud.user.model.request.UpdateUserRequest
import com.dunkware.cloud.user.model.response.UserResponse
import com.dunkware.cloud.user.service.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody request: CreateUserRequest): UserResponse {
        return userService.createUser(request) ;
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): UserResponse {
        return userService.getUser(id)
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody request: UpdateUserRequest
    ): UserResponse {
        return userService.updateUser(id, request)
    }

    @PostMapping("/validate")
    fun validateCredentials(
        @RequestBody request: CredentialsValidationRequest
    ): UserResponse {
        return userService.validateCredentials(request.username, request.password)
    }
}

data class CredentialsValidationRequest(
    val username: String,
    val password: String
)
