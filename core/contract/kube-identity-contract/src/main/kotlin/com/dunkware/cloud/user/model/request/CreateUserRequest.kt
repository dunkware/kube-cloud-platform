package com.dunkware.cloud.user.model.request

data class CreateUserRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val roles: Set<String> = emptySet()
)
