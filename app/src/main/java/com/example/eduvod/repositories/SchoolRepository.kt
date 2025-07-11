package com.example.eduvod.repositories

import com.example.eduvod.model.*
import com.example.eduvod.retrofit.ApiClient
import com.example.eduvod.retrofit.ApiClient.apiService
import com.example.eduvod.retrofit.response.ApiResponse
import com.example.eduvod.retrofit.response.SchoolResponse
import com.example.eduvod.viewmodel.AdminAssignRequest
import com.example.eduvod.viewmodel.AdminCreateRequest
import com.example.eduvod.viewmodel.AdminResetRequest
import com.example.eduvod.viewmodel.AdminUnassignRequest
import com.example.eduvod.viewmodel.SchoolAdmin
import com.example.eduvod.viewmodel.SchoolRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

class SchoolRepository {

    private val api = ApiClient.apiService

    // SCHOOLS
    suspend fun getSchools(): Response<ApiResponse<List<School>>> {
        return api.getSchools()
    }

    suspend fun addSchool(schoolRequest: SchoolRequest): Response<ApiResponse<SchoolResponse>> {
        return api.addSchool(schoolRequest)
    }

    suspend fun updateSchool(id: Int, request: School): Response<ApiResponse<School>> {
        return api.updateSchool(id, request)
    }
    suspend fun getSchoolById(id: Int): Response<ApiResponse<School>> {
        return api.getSchoolById(id)
    }

    suspend fun deleteSchool(id: Int): Response<ApiResponse<Unit>> {
        return api.deleteSchool(id)
    }

    suspend fun downloadTemplate(): Response<ResponseBody> {
        return api.downloadTemplate()
    }

    suspend fun importSchools(file: MultipartBody.Part): Response<ApiResponse<Unit>> {
        return api.importSchools(file)
    }

    // SCHOOL ADMINS

    suspend fun getAllSchoolAdmins(): Response<ApiResponse<List<SchoolAdmin>>> {
        return api.getAllSchoolAdmins()
    }

    suspend fun addSchoolAdmin(request: AdminCreateRequest): Response<ApiResponse<SchoolAdmin>> {
        return api.addSchoolAdmin(request)
    }

    suspend fun assignAdminToSchool(request: AdminAssignRequest): Response<ApiResponse<Unit>> {
        return api.assignAdminToSchool(request)
    }

    suspend fun updateAdminStatus(id: Int, status: String): Response<ApiResponse<Unit>> {
        return api.updateSchoolAdminStatus(id, status)
    }

    suspend fun resetAdminPassword(id: Int, request: AdminResetRequest): Response<ApiResponse<Unit>> {
        return api.resetPassword(id, request)
    }

    suspend fun unassignAdmin(request: AdminUnassignRequest): Response<ApiResponse<Unit>> {
        return apiService.unassignAdmin(request)
    }

    suspend fun deleteSchoolAdmin(id: String): Response<ApiResponse<Unit>> {
        return api.deleteSchoolAdmin(id)
    }
}
