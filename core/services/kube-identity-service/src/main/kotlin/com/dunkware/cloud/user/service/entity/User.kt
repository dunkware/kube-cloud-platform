package com.dunkware.cloud.user.service.entity

import jakarta.persistence.*
import java.time.OffsetDateTime
@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false, length = 50)
    var username: String,

    @Column(unique = true, nullable = false, length = 100)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Column(name = "first_name", length = 50)
    var firstName: String? = null,

    @Column(name = "last_name", length = 50)
    var lastName: String? = null,

    var enabled: Boolean = true,

    @Column(name = "account_non_expired")
    var accountNonExpired: Boolean = true,

    @Column(name = "credentials_non_expired")
    var credentialsNonExpired: Boolean = true,

    @Column(name = "account_non_locked")
    var accountNonLocked: Boolean = true,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<Role> = mutableSetOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var permissions: MutableSet<UserPermission> = mutableSetOf(),

    @Column(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
) {
    // No-args constructor required by JPA
    constructor() : this(
        username = "",
        email = "",
        password = ""
    )
}