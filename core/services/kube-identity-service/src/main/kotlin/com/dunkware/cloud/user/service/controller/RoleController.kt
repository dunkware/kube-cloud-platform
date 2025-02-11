package com.dunkware.cloud.user.service.controller

import com.dunkware.cloud.user.domain.RoleDto
import com.dunkware.cloud.user.model.request.CreateRoleRequest
import com.dunkware.cloud.user.model.request.UpdateRoleRequest

import com.dunkware.cloud.user.service.service.RoleService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/roles")
class RoleController(
    private val roleService: RoleService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createRole(@RequestBody request: CreateRoleRequest): RoleDto {
        return roleService.createRole(request)
    }

    @PutMapping("/{name}")
    fun updateRole(
        @PathVariable name: String,
        @RequestBody request: UpdateRoleRequest
    ): RoleDto {
        return roleService.updateRole(request.name.toString(), request)
    }

    @GetMapping
    fun getAllRoles(): List<RoleDto> {
        return roleService.getAllRoles()
    }
}
