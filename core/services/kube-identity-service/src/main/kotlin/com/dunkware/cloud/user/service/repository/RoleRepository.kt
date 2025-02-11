package com.dunkware.cloud.user.service.repository

import com.dunkware.cloud.user.service.entity.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(name: String): Optional<Role>

    fun existsByName(name: String): Boolean

    fun findByNameIn(names: Collection<String>): List<Role>

    @Query("""
        SELECT DISTINCT r FROM Role r
        LEFT JOIN FETCH r.permissions p
        WHERE (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:permission IS NULL OR p.name = :permission)
    """)
    fun searchRoles(
        @Param("name") name: String?,
        @Param("permission") permission: String?,
        pageable: Pageable
    ): Page<Role>
}