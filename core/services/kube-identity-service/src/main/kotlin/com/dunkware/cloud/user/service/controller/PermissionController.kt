package com.dunkware.cloud.user.service.controller

import com.dunkware.cloud.user.domain.PermissionDto
import com.dunkware.cloud.user.model.request.CreatePermissionRequest

import com.dunkware.cloud.user.service.service.PermissionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/permissions")
class PermissionController(
    private val permissionService: PermissionService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPermission(@RequestBody request: CreatePermissionRequest): PermissionDto {
        return permissionService.createPermission(request)
    }

    @GetMapping
    fun getAllPermissions(): List<PermissionDto> {
        return permissionService.getAllPermissions()
    }
}