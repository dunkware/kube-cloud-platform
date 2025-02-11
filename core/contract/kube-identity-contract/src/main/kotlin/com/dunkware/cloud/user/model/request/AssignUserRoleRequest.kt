package com.dunkware.cloud.user.model.request


data class AssignUserRolesRequest(
    val roles: Set<String>
)