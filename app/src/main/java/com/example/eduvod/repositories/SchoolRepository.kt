package com.example.eduvod.repositories

import com.example.eduvod.retrofit.ApiClient
import com.example.eduvod.model.School
import com.example.eduvod.retrofit.response.ApiResponse
import com.example.eduvod.retrofit.response.SchoolResponse
import com.example.eduvod.viewmodel.AdminAssignRequest
import com.example.eduvod.viewmodel.AdminBlockRequest
import com.example.eduvod.viewmodel.AdminCreateRequest
import com.example.eduvod.viewmodel.AdminResetRequest
import com.example.eduvod.viewmodel.AdminUnassignRequest
import com.example.eduvod.viewmodel.SchoolAdmin
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

class SchoolRepository {

    private val api = ApiClient.apiService

    suspend fun getSchools(): Response<ApiResponse<List<School>>> {
       return api.getSchools()
    }

    suspend fun addSchool(school: School): Response<ApiResponse<SchoolResponse>> {
        return api.addSchool(school)
    }

    suspend fun updateSchool(id: Int, school: School): Response<ApiResponse<School>> {
        return api.updateSchool(id, school)
    }

    suspend fun getSchoolById(id: Int): Response<ApiResponse<School>> {
        return api.getSchoolById(id)
    }

    suspend fun deleteSchool(id: Int): Response<ApiResponse<Unit>> {
        return api.deleteSchool(id)
    }

    suspend fun assignSchoolAdmin(schoolId: Int, email: String): Response<ApiResponse<Unit>> {
        return api.assignAdmin(schoolId, mapOf("email" to email))
    }

    suspend fun unassignSchoolAdmin(schoolId: Int): Response<ApiResponse<Unit>> {
        return api.unassignAdmin(schoolId)
    }

    suspend fun downloadTemplate(): Response<ResponseBody> {
        return api.downloadTemplate()
    }

    suspend fun importSchools(file: MultipartBody.Part): Response<ApiResponse<Unit>> {
        return api.importSchools(file)
    }

    suspend fun getAllSchoolAdmins(): Response<ApiResponse<List<SchoolAdmin>>> {
        return api.getAllSchoolAdmins()
    }

    suspend fun addSchoolAdmin(request: AdminCreateRequest): Response<ApiResponse<SchoolAdmin>> {
        return api.addSchoolAdmin(request)
    }

    suspend fun assignAdminToSchool(request: AdminAssignRequest): Response<ApiResponse<Unit>> {
        return api.assignAdmin(request)
    }

    suspend fun unassignAdmin(request: AdminUnassignRequest): Response<ApiResponse<Unit>> {
        return api.unassignAdmin(request)
    }

    suspend fun resetAdminPassword(request: AdminResetRequest): Response<ApiResponse<Unit>> {
        return api.resetPassword(request)
    }

    suspend fun blockOrUnblockAdmin(request: AdminBlockRequest): Response<ApiResponse<Unit>> {
        return api.blockAdmin(request)
    }

}