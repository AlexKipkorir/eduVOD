package com.example.eduvod.model

data class AdminUser(
    val email: String,
    var isBlocked: Boolean = false,
    var assignedSchool: String? = null
)
