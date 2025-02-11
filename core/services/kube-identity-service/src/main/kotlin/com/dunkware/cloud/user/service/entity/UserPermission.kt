package com.dunkware.cloud.user.service.entity

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "user_permissions")
class UserPermission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permission_id", nullable = false)
    var permission: Permission,

    var granted: Boolean = true,

    @Column(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
) {
    constructor() : this(user = User(), permission = Permission(), granted = true, createdAt = OffsetDateTime.now(), updatedAt = OffsetDateTime.now())
}