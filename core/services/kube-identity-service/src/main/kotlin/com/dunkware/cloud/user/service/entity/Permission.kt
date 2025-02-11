package com.dunkware.cloud.user.service.entity

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "permissions")
class Permission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false, length = 100)
    var name: String,

    var description: String? = null,

    @Column(nullable = false, length = 50)
    var resource: String,

    @Column(nullable = false, length = 50)
    var action: String,

    @Column(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
) {
    constructor() : this(
        id = null,
        name = "",
        description = null,
        resource = "",
        action = "",
        createdAt = OffsetDateTime.now(),
        updatedAt = OffsetDateTime.now()
    )
}