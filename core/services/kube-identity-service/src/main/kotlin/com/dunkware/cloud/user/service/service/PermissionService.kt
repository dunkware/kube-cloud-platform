package com.dunkware.cloud.user.service.service

import com.dunkware.cloud.user.model.request.UpdatePermissionRequest



import com.dunkware.cloud.user.domain.PermissionDto
import com.dunkware.cloud.user.model.request.CreatePermissionRequest

import com.dunkware.cloud.user.service.entity.Permission
import com.dunkware.cloud.user.service.mapper.toDto
import com.dunkware.cloud.user.service.repository.PermissionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class PermissionService(
    private val permissionRepository: PermissionRepository
) {

    @Transactional
    fun createPermission(request: CreatePermissionRequest): PermissionDto {
        if (permissionRepository.findByName(request.name).isPresent) {
            throw IllegalArgumentException("Permission with name ${request.name} already exists")
        }

        if (permissionRepository.findByResourceAndAction(request.resource, request.action).isPresent) {
            throw IllegalArgumentException("Permission for ${request.resource}:${request.action} already exists")
        }

        val permission = Permission(
            name = request.name,
            resource = request.resource,
            action = request.action,
            description = request.description
        )

        return permissionRepository.save(permission).toDto()
    }

    fun updatePermission(id: Long, request: UpdatePermissionRequest): PermissionDto {
        val permission = permissionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Permission not found with id: $id") }

        request.name?.let { newName ->
            permissionRepository.findByName(newName)
                .filter { it.id != id }
                .ifPresent { throw IllegalArgumentException("Permission with name $newName already exists") }
            permission.name = newName
        }

        request.description?.let { permission.description = it }

        // Handle resource and action updates together
        val resourceUpdate = request.resource
        val actionUpdate = request.action

        if (resourceUpdate != null && actionUpdate != null) {
            // Both are provided, check for conflicts and update
            permissionRepository.findByResourceAndAction(resourceUpdate, actionUpdate)
                .filter { it.id != id }
                .ifPresent {
                    throw IllegalArgumentException(
                        "Permission for $resourceUpdate:$actionUpdate already exists"
                    )
                }
            permission.resource = resourceUpdate
            permission.action = actionUpdate
        } else if (resourceUpdate != null || actionUpdate != null) {
            // One is provided but not the other
            throw IllegalArgumentException("Both resource and action must be provided together")
        }

        permission.updatedAt = OffsetDateTime.now()
        return permissionRepository.save(permission).toDto()
    }


    @Transactional(readOnly = true)
    fun getPermissionByName(name: String): PermissionDto {
        return permissionRepository.findByName(name)
            .orElseThrow { IllegalArgumentException("Permission not found with name: $name") }
            .toDto()
    }

    @Transactional(readOnly = true)
    fun getAllPermissions(pageable: Pageable): Page<PermissionDto> {
        return permissionRepository.findAll(pageable).map { it.toDto() }
    }




    @Transactional(readOnly = true)
    fun getAllPermissions(): List<PermissionDto> {
        return permissionRepository.findAll().map { permission: Permission -> permission.toDto() }
    }

    @Transactional(readOnly = true)
    fun getPermission(id: Long): PermissionDto {
        return permissionRepository.findById(id)
            .map { permission: Permission -> permission.toDto() }
            .orElseThrow { IllegalArgumentException("Permission not found with id: $id") }
    }

    @Transactional(readOnly = true)
    fun getPermissionsByResource(resource: String): List<PermissionDto> {
        return permissionRepository.findByResource(resource).map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun findByResourceAndAction(resource: String, action: String): PermissionDto? {
        return permissionRepository.findByResourceAndAction(resource, action)
            .map { it.toDto() }
            .orElse(null)
    }

    @Transactional
    fun deletePermission(id: Long) {
        if (!permissionRepository.existsById(id)) {
            throw IllegalArgumentException("Permission not found with id: $id")
        }
        // Note: This might fail if the permission is referenced by roles
        // You might want to add a check for that
        permissionRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun validatePermissions(permissionNames: Set<String>): Set<Permission> {
        val permissions = permissionRepository.findByNameIn(permissionNames)
        val foundPermissionNames = permissions.map { it.name }.toSet()
        val missingPermissions = permissionNames - foundPermissionNames

        if (missingPermissions.isNotEmpty()) {
            throw IllegalArgumentException(
                "Following permissions not found: ${missingPermissions.joinToString(", ")}"
            )
        }

        return permissions.toSet()
    }

    @Transactional(readOnly = true)
    fun searchPermissions(
        resource: String? = null,
        action: String? = null,
        name: String? = null,
        pageable: Pageable
    ): Page<PermissionDto> {
        return permissionRepository.searchPermissions(resource, action, name, pageable)
            .map { it.toDto() }
    }
}