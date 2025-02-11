package com.dunkware.cloud.user.model.request

data class UpdateUserRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val roles: Set<String>? = null
)
