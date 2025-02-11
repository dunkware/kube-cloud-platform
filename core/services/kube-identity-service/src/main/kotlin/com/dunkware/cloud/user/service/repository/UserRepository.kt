package com.dunkware.cloud.user.service.repository

import com.dunkware.cloud.user.service.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): Optional<User>

    fun findByEmail(email: String): Optional<User>

    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN FETCH u.roles r
        LEFT JOIN FETCH r.permissions
        LEFT JOIN FETCH u.permissions
        WHERE u.username = :username
    """)
    fun findByUsernameWithAuthorities(username: String): Optional<User>

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean

    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN u.roles r
        WHERE (:query IS NULL 
            OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(COALESCE(u.firstName, '')) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(COALESCE(u.lastName, '')) LIKE LOWER(CONCAT('%', :query, '%')))
        AND (:role IS NULL OR r.name = :role)
        AND (:enabled IS NULL OR u.enabled = :enabled)
    """)
    fun searchUsers(
        @Param("query") query: String?,
        @Param("role") role: String?,
        @Param("enabled") enabled: Boolean?,
        pageable: Pageable
    ): Page<User>

    // Optional: Add a non-paginated version if needed
    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN u.roles r
        WHERE (:query IS NULL 
            OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(COALESCE(u.firstName, '')) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(COALESCE(u.lastName, '')) LIKE LOWER(CONCAT('%', :query, '%')))
        AND (:role IS NULL OR r.name = :role)
        AND (:enabled IS NULL OR u.enabled = :enabled)
    """)
    fun searchUsers(
        @Param("query") query: String?,
        @Param("role") role: String?,
        @Param("enabled") enabled: Boolean?
    ): List<User>
}