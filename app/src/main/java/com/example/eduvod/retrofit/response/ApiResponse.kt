package com.example.eduvod.retrofit.response

data class ApiResponse<T>(
    val statusCode: Int,
    val message: String,
    val data: T?
)