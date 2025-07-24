package com.example.eduvod.repositories

import android.util.Log
import com.example.eduvod.model.SuperAdminDashboardResponse
import com.example.eduvod.retrofit.ApiClient.apiService

class DashboardRepository {
    suspend fun getDashboardStats(): Result<SuperAdminDashboardResponse> {
        return try {
            val response = apiService.getSuperAdminDashboard()
            if (response.isSuccessful) {
                Log.d("DashboardRepo", "Calling dashboard API")
                val body = response.body()
                if (body != null && body.statusCode == 200 && body.data != null) {
                    Result.success(body.data!!)
                } else {
                    Result.failure(Exception("Unexpected response format or error: ${body?.message}"))
                }
            } else {
                Result.failure(Exception("Server error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

