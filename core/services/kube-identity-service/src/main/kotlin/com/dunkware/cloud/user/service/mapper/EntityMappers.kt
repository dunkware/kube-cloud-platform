package com.dunkware.cloud.user.service.mapper

import com.dunkware.cloud.user.domain.PermissionDto
import com.dunkware.cloud.user.domain.RoleDto
import com.dunkware.cloud.user.domain.UserDto
import com.dunkware.cloud.user.service.entity.Permission
import com.dunkware.cloud.user.service.entity.Role
import com.dunkware.cloud.user.service.entity.User

fun User.toDto() = UserDto(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    enabled = enabled,
    roles = roles.map { it.toDto() }.toSet()
)

fun Role.toDto() = RoleDto(
    id = id,
    name = name,
    description = description,
    permissions = permissions.map { it.toDto() }.toSet()
)

fun Permission.toDto() = PermissionDto(
    id = id,
    name = name,
    resource = resource,
    action = action,
    description = description
)