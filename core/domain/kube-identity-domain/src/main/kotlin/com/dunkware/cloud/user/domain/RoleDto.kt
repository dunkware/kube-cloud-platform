package com.dunkware.cloud.user.domain

data class RoleDto(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val permissions: Set<PermissionDto> = emptySet()
)
