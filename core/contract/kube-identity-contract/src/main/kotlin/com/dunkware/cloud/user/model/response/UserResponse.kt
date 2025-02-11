package com.dunkware.cloud.user.model.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class UserResponse @JsonCreator constructor(
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("username")
    val username: String,

    @JsonProperty("email")
    val email: String,

    @JsonProperty("firstName")
    val firstName: String?,

    @JsonProperty("lastName")
    val lastName: String?,

    @JsonProperty("roles")
    val roles: Set<String>,

    @JsonProperty("permissions")
    val permissions: Set<String>
) {
    // No-args constructor for Jackson
    constructor() : this(
        id = 0,
        username = "",
        email = "",
        firstName = null,
        lastName = null,
        roles = emptySet(),
        permissions = emptySet()
    )
}