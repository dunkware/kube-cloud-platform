package com.dunkware.cloud.user.service.controller

import com.dunkware.cloud.user.domain.UserDto
import com.dunkware.cloud.user.model.request.AssignUserRolesRequest

import com.dunkware.cloud.user.service.service.UserRoleService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/{userId}/roles")
class UserRoleController(
    private val userRoleService: UserRoleService
) {

    @PutMapping
    fun assignRoles(
        @PathVariable userId: Long,
        @RequestBody request: AssignUserRolesRequest
    ): UserDto {
        return userRoleService.assignRoles(userId, request)
    }
}