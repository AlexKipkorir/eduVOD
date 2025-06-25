package com.example.eduvod.repositories

import com.example.eduvod.retrofit.request.LoginRequest
import com.example.eduvod.retrofit.response.LoginResponseData
import com.example.eduvod.retrofit.ApiClient
import com.example.eduvod.retrofit.response.ApiResponse
import retrofit2.Response

class LoginRepository {

    suspend fun loginUser(request: LoginRequest): Response<ApiResponse<LoginResponseData>> {
        return ApiClient.apiService.login(request)
    }
}