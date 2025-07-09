package com.example.eduvod.repositories

import com.example.eduvod.model.Grade
import com.example.eduvod.model.GradeRequest
import com.example.eduvod.retrofit.ApiClient
import com.example.eduvod.retrofit.ApiService
import com.example.eduvod.retrofit.response.ApiResponse
import retrofit2.Response

class GradesRepository(
    private val api: ApiService = ApiClient.apiService
) {

    suspend fun getGrades(): Response<ApiResponse<List<Grade>>> {
        return api.getAllGrades()
    }

    suspend fun addGrade(request: GradeRequest): Response<ApiResponse<Grade>> {
        return api.addGrade(request)
    }
    suspend fun deleteGrade(id: Int): Response<ApiResponse<String>> {
        return api.deleteGrade(id)
    }
//    suspend fun addStreamToGrade(gradeName: String, stream: Stream): Response<ApiResponse<Unit>> {
//        return api.addStreamToGrade(gradeName, stream)
//    }
//
//    suspend fun deleteStream(gradeName: String, streamName: String): Response<ApiResponse<Unit>> {
//        return api.deleteStream(gradeName, streamName)
//    }
//
//    suspend fun renameStream(gradeName: String, oldName: String, newName: String): Response<ApiResponse<Unit>> {
//        return api.renameStream(gradeName, RenameStreamRequest(oldName, newName))
//    }
}