package com.dunkware.cloud.user.service.repository

import com.dunkware.cloud.user.service.entity.Permission
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface PermissionRepository : JpaRepository<Permission, Long> {
    fun findByName(name: String): Optional<Permission>

    fun findByResourceAndAction(resource: String, action: String): Optional<Permission>

    fun findByResource(resource: String): List<Permission>

    fun findByNameIn(names: Collection<String>): List<Permission>

    @Query("""
        SELECT p FROM Permission p
        WHERE (:resource IS NULL OR LOWER(p.resource) LIKE LOWER(CONCAT('%', :resource, '%')))
        AND (:action IS NULL OR LOWER(p.action) LIKE LOWER(CONCAT('%', :action, '%')))
        AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
        ORDER BY p.name ASC
    """)
    fun searchPermissions(
        @Param("resource") resource: String?,
        @Param("action") action: String?,
        @Param("name") name: String?,
        pageable: Pageable
    ): Page<Permission>

    // Overloaded method without pagination
    @Query("""
        SELECT p FROM Permission p
        WHERE (:resource IS NULL OR LOWER(p.resource) LIKE LOWER(CONCAT('%', :resource, '%')))
        AND (:action IS NULL OR LOWER(p.action) LIKE LOWER(CONCAT('%', :action, '%')))
        AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
        ORDER BY p.name ASC
    """)
    fun searchPermissions(
        @Param("resource") resource: String?,
        @Param("action") action: String?,
        @Param("name") name: String?
    ): List<Permission>
}