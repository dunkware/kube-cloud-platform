package com.dunkware.cloud.user.service.service

import com.dunkware.cloud.user.domain.RoleDto
import com.dunkware.cloud.user.model.request.CreateRoleRequest
import com.dunkware.cloud.user.model.request.UpdateRoleRequest
import com.dunkware.cloud.user.service.entity.Role
import com.dunkware.cloud.user.service.mapper.toDto
import com.dunkware.cloud.user.service.repository.PermissionRepository
import com.dunkware.cloud.user.service.repository.RoleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class RoleService(
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository
) {
    @Transactional
    fun createRole(request: CreateRoleRequest): RoleDto {
        if (roleRepository.existsByName(request.name)) {
            throw IllegalArgumentException("Role with name ${request.name} already exists")
        }

        val permissions = request.permissions.mapNotNull { permName ->
            permissionRepository.findByName(permName)
                .orElseThrow { IllegalArgumentException("Permission not found: $permName") }
        }.toMutableSet()

        val role = Role(
            name = request.name,
            description = request.description,
            permissions = permissions
        )

        return roleRepository.save(role).toDto()
    }


    @Transactional
    fun updateRole(name: String, request: UpdateRoleRequest): RoleDto {
        val role = roleRepository.findByName(name)
            .orElseThrow { IllegalArgumentException("Role not found with name: $name") }

        request.name?.let { newName ->
            if (roleRepository.existsByName(newName) && role.name != newName) {
                throw IllegalArgumentException("Role with name $newName already exists")
            }
            role.name = newName
        }

        request.description?.let { role.description = it }

        request.permissions?.let { permissionNames ->
            val permissions = permissionNames.mapNotNull { permName ->
                permissionRepository.findByName(permName)
                    .orElseThrow { IllegalArgumentException("Permission not found: $permName") }
            }.toMutableSet()
            role.permissions = permissions
        }

        role.updatedAt = OffsetDateTime.now()
        return roleRepository.save(role).toDto()
    }



@Transactional(readOnly = true)
    fun getRole(id: Long): RoleDto {
        return roleRepository.findById(id)
            .map { role: Role -> role.toDto() }
            .orElseThrow { IllegalArgumentException("Role not found with id: $id") }
    }

    @Transactional(readOnly = true)
    fun getRoleByName(name: String): RoleDto {
        return roleRepository.findByName(name)
            .map { role: Role -> role.toDto() }
            .orElseThrow { IllegalArgumentException("Role not found with name: $name") }
    }

    @Transactional(readOnly = true)
    fun getAllRoles(pageable: Pageable): Page<RoleDto> {
        return roleRepository.findAll(pageable)
            .map { role: Role -> role.toDto() }
    }

    @Transactional(readOnly = true)
    fun getAllRoles(): List<RoleDto> {
        return roleRepository.findAll()
            .map { role: Role -> role.toDto() }
    }

    @Transactional
    fun deleteRole(id: Long) {
        if (!roleRepository.existsById(id)) {
            throw IllegalArgumentException("Role not found with id: $id")
        }
        roleRepository.deleteById(id)
    }

    @Transactional
    fun addPermissionsToRole(roleId: Long, permissionNames: Set<String>): RoleDto {
        val role = roleRepository.findById(roleId)
            .orElseThrow { IllegalArgumentException("Role not found with id: $roleId") }

        val newPermissions = permissionNames.mapNotNull { permName ->
            permissionRepository.findByName(permName)
                .orElseThrow { IllegalArgumentException("Permission not found: $permName") }
        }

        role.permissions.addAll(newPermissions)
        role.updatedAt = OffsetDateTime.now()

        return roleRepository.save(role).toDto()
    }

    @Transactional
    fun removePermissionsFromRole(roleId: Long, permissionNames: Set<String>): RoleDto {
        val role = roleRepository.findById(roleId)
            .orElseThrow { IllegalArgumentException("Role not found with id: $roleId") }

        val permissionsToRemove = role.permissions.filter { it.name in permissionNames }
        role.permissions.removeAll(permissionsToRemove.toSet())
        role.updatedAt = OffsetDateTime.now()

        return roleRepository.save(role).toDto()
    }

    @Transactional(readOnly = true)
    fun getRolePermissions(roleId: Long): Set<String> {
        val role = roleRepository.findById(roleId)
            .orElseThrow { IllegalArgumentException("Role not found with id: $roleId") }

        return role.permissions.map { it.name }.toSet()
    }

    @Transactional(readOnly = true)
    fun validateRoles(roleNames: Set<String>): Set<Role> {
        val roles = roleRepository.findByNameIn(roleNames)
        val foundRoleNames = roles.map { it.name }.toSet()
        val missingRoles = roleNames - foundRoleNames

        if (missingRoles.isNotEmpty()) {
            throw IllegalArgumentException(
                "Following roles not found: ${missingRoles.joinToString(", ")}"
            )
        }

        return roles.toSet()
    }

    @Transactional(readOnly = true)
    fun searchRoles(
        name: String?,
        hasPermission: String?,
        pageable: Pageable
    ): Page<RoleDto> {
        return roleRepository.searchRoles(name, hasPermission, pageable)
            .map { role: Role -> role.toDto() }
    }
}