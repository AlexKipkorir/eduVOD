package com.example.eduvod.repositories

import com.example.eduvod.retrofit.ApiClient
import com.example.eduvod.retrofit.response.ApiResponse
import com.example.eduvod.viewmodel.DashboardStats
import retrofit2.Response

class DashboardRepository {
    suspend fun getDashboardStats(): Response<ApiResponse<DashboardStats>> {
        return ApiClient.apiService.getDashboardStats()
    }
}