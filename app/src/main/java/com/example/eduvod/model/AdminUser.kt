package com.example.eduvod.model

import androidx.compose.runtime.mutableStateOf

data class AdminUser(
    val email: String,
    val isBlocked: androidx.compose.runtime.MutableState<Boolean> = mutableStateOf(false)
)
