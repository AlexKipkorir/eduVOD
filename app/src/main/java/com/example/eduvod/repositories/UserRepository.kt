package com.example.eduvod.repositories

import com.example.eduvod.retrofit.ApiClient
import com.example.eduvod.retrofit.response.ApiResponse
import com.example.eduvod.viewmodel.AdminBlockRequest
import com.example.eduvod.viewmodel.AdminEduvodCreateRequest
import com.example.eduvod.viewmodel.AdminEduvodResetRequest
import com.example.eduvod.viewmodel.AdminUser
import retrofit2.Response

class UserRepository {
    private val api = ApiClient.apiService

    suspend fun registerSuperAdmin(request: AdminEduvodCreateRequest): Response<ApiResponse<Unit>> {
        return api.registerSuperAdmin(request)
    }

    suspend fun getAllUsers(): Response<ApiResponse<List<AdminUser>>> {
        return api.getAllUsers()
    }

    suspend fun updateUserStatus(id: Long, status: String): Response<ApiResponse<Unit>> {
        return api.updateUserStatus(id, status)
    }

    suspend fun deleteUser(id: Long): Response<ApiResponse<Unit>> {
        return api.deleteUser(id)
    }

    suspend fun resetEduvodAdmin(request: AdminEduvodResetRequest): Response<ApiResponse<Unit>> {
        return api.resetEduvodAdmin(request)
    }
}