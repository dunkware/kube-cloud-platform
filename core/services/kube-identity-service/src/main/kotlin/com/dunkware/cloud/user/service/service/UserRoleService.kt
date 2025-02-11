package com.dunkware.cloud.user.service.service

import com.dunkware.cloud.user.domain.UserDto
import com.dunkware.cloud.user.model.request.AssignUserRolesRequest

import com.dunkware.cloud.user.service.repository.RoleRepository
import com.dunkware.cloud.user.service.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class UserRoleService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) {
    
    @Transactional
    fun assignRoles(userId: Long, request: AssignUserRolesRequest): UserDto {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found: $userId") }

        val roles = request.roles.mapNotNull { roleName ->
            roleRepository.findByName(roleName)
                .orElseThrow { IllegalArgumentException("Role not found: $roleName") }
        }.toMutableSet()

        user.roles = roles
        user.updatedAt = OffsetDateTime.now()

        return userRepository.save(user).toDto()
    }

    private fun com.dunkware.cloud.user.service.entity.User.toDto() = UserDto(
        id = id,
        username = username,
        email = email,
        firstName = firstName,
        lastName = lastName,
        enabled = enabled,
        //roles = roles.map { (com.dunkware.cloud.user.service.mapper.UserRoleService).Role.toDto() }.toSet()
    )
}