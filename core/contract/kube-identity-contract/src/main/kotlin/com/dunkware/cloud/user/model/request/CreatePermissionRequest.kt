package com.dunkware.cloud.user.model.request


data class CreatePermissionRequest(
    val name: String,
    val resource: String,
    val action: String,
    val description: String? = null
)
