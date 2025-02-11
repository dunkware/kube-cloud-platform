package com.dunkware.cloud.user.domain

data class PermissionDto(
    val id: Long? = null,
    val name: String,
    val resource: String,
    val action: String,
    val description: String? = null
)
