package com.dunkware.cloud.user.model.request

data class UpdateRoleRequest(
    val description: String? = null,
    val permissions: Set<String>? = null,
    val name: String? = null
)

