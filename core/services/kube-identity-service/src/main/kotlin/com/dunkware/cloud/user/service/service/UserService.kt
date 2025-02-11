package com.dunkware.cloud.user.service.service

import com.dunkware.cloud.user.domain.UserDetailsDto
import com.dunkware.cloud.user.model.request.CreateUserRequest
import com.dunkware.cloud.user.model.request.UpdateUserRequest
import com.dunkware.cloud.user.model.response.UserResponse
import com.dunkware.cloud.user.service.entity.User
import com.dunkware.cloud.user.service.repository.RoleRepository
import com.dunkware.cloud.user.service.repository.UserRepository
import io.jsonwebtoken.security.Keys.password
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    //private val passwordEncoder: PasswordEncoder
) {

    private val logger = LoggerFactory.getLogger(UserService::class.java);

    @Transactional
    fun createUser(request: CreateUserRequest): UserResponse {
        if (userRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("Username ${request.username} already exists")
        }
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email ${request.email} already exists")
        }

        val roles = request.roles.mapNotNull { roleName ->
            roleRepository.findByName(roleName)
                .orElseThrow { IllegalArgumentException("Role not found: $roleName") }
        }.toMutableSet()

        val user = User(
            username = request.username,
            email = request.email,
            password = request.password,
            firstName = request.firstName,
            lastName = request.lastName,
            roles = roles
        )

        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun updateUser(id: Long, request: UpdateUserRequest): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found with id: $id") }

        request.email?.let { newEmail ->
            if (userRepository.existsByEmail(newEmail) && user.email != newEmail) {
                throw IllegalArgumentException("Email $newEmail already exists")
            }
            user.email = newEmail
        }

        request.firstName?.let { user.firstName = it }
        request.lastName?.let { user.lastName = it }

        request.roles?.let { roleNames ->
            val roles = roleNames.mapNotNull { roleName ->
                roleRepository.findByName(roleName)
                    .orElseThrow { IllegalArgumentException("Role not found: $roleName") }
            }.toMutableSet()
            user.roles = roles
        }

        user.updatedAt = OffsetDateTime.now()
        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun updatePassword(id: Long, currentPassword: String, newPassword: String) {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found with id: $id") }

        if (currentPassword == user.password == false) {
            throw IllegalArgumentException("Current password is incorrect")
        }

        user.password = newPassword; // passwordEncoder.encode(newPassword)
        user.updatedAt = OffsetDateTime.now()
        userRepository.save(user)
    }

    @Transactional
    fun toggleUserStatus(id: Long, enabled: Boolean): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found with id: $id") }

        user.enabled = enabled
        user.updatedAt = OffsetDateTime.now()
        return userRepository.save(user).toResponse()
    }

    @Transactional(readOnly = true)
    fun getUser(id: Long): UserResponse {
        return userRepository.findById(id)
            .map { user: User -> user.toResponse() }
            .orElseThrow { IllegalArgumentException("User not found with id: $id") }
    }

    @Transactional(readOnly = true)
    fun getUserByUsername(username: String): UserResponse {
        return userRepository.findByUsername(username)
            .map { user: User -> user.toResponse() }
            .orElseThrow { IllegalArgumentException("User not found with username: $username") }
    }

    @Transactional(readOnly = true)
    fun validateCredentials(username: String, password: String): UserResponse {
        val user = userRepository.findByUsernameWithAuthorities(username)
            .orElseThrow { IllegalArgumentException("Invalid credentials") }
        logger.info("validate username $username" + password + user.password)

        if (password.equals(user.password) == false) {
            throw IllegalArgumentException("Invalid credentials")
        }

        if (!user.enabled) {
            throw IllegalArgumentException("User account is disabled")
        }

        return user.toResponse()
    }

    @Transactional(readOnly = true)
    fun getAllUsers(pageable: Pageable): Page<UserResponse> {
        return userRepository.findAll(pageable)
            .map { user: User -> user.toResponse() }
    }

    @Transactional(readOnly = true)
    fun searchUsers(
        query: String?,
        role: String?,
        enabled: Boolean?,
        pageable: Pageable
    ): Page<UserResponse> {
        return userRepository.searchUsers(query, role, enabled, pageable)
            .map { user: User -> user.toResponse() }
    }

    @Transactional
    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw IllegalArgumentException("User not found with id: $id")
        }
        userRepository.deleteById(id)
    }

    private fun User.toResponse() = UserResponse(
        id = id!!,
        username = username,
        email = email,
        firstName = firstName,
        lastName = lastName,
        roles = roles.map { it.name }.toSet(),
        permissions = roles.flatMap { role ->
            role.permissions.map { it.name }
        }.toSet()
    )
}