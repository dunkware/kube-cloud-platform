package com.dunkware.cloud.user.domain



data class UserDetailsDto (
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val roles: Set<String>,
    val permissions: Set<String>
)