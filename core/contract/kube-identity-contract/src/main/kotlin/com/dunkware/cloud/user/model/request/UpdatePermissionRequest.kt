package com.dunkware.cloud.user.model.request
data class UpdatePermissionRequest(
    val name: String? = null,
    val resource: String? = null,
    val action: String? = null,
    val description: String? = null
)