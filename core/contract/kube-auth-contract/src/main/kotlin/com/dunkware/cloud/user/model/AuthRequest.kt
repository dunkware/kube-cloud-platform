package com.dunkware.cloud.user.model

data class AuthRequest(
    val username: String,
    val password: String
) {
    // Default constructor for Jackson
    constructor() : this("", "")
}
