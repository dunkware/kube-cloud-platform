package com.dunkware.cloud.user.model.request

data class CreateRoleRequest(
    val name: String,
    val description: String? = null,
    val permissions: Set<String> = emptySet()
)

