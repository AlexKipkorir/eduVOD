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

    suspend fun getAllEduvodAdmins(): Response<ApiResponse<List<AdminUser>>> {
        return api.getAllEduvodAdmins()
    }

    suspend fun addEduvodAdmins(request: AdminEduvodCreateRequest): Response<ApiResponse<Unit>> {
        return api.addEduvodAdmin(request)
    }

    suspend fun blockEduvodAdmin(request: AdminBlockRequest): Response<ApiResponse<Unit>> {
        return api.blockEduvodAdmin(request)
    }

    suspend fun resetEduvodAdmin(request: AdminEduvodResetRequest): Response<ApiResponse<Unit>> {
        return api.resetEduvodAdmin(request)
    }
}