package com.dunkware.cloud.user.domain

data class UserDto(
    val id: Long? = null,
    val username: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val enabled: Boolean = true,
    val roles: Set<RoleDto> = emptySet()
)
