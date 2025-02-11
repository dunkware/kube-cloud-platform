package com.dunkware.cloud.user.model.request

data class AssignRolePermissionsRequest(
    val permissions: Set<String>
)
