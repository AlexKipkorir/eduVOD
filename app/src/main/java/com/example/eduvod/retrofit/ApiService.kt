package com.example.eduvod.retrofit

import com.example.eduvod.model.School
import com.example.eduvod.retrofit.request.LoginRequest
import com.example.eduvod.retrofit.response.ApiResponse
import com.example.eduvod.retrofit.response.LoginResponseData
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
import retrofit2.http.*


interface ApiService {
    //Login
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponseData>>

    //School Management
    @GET("schools")
    suspend fun getSchools(): Response<ApiResponse<List<School>>>

    @POST("schools")
    suspend fun addSchool(@Body school: School): Response<ApiResponse<SchoolResponse>>

    @PUT("schools/{id}")
    suspend fun updateSchool(@Path("id") id: Int, @Body school: School): Response<ApiResponse<School>>

    @GET("schools/{id}")
    suspend fun getSchoolById(@Path("id") id: Int): Response<ApiResponse<School>>

    @POST("schools/{id}/assign-admin")
    suspend fun assignAdmin(@Path("id") id: Int, @Body adminEmail: Map<String, String>): Response<ApiResponse<Unit>>

    @DELETE("schools/{id}/admin")
    suspend fun unassignAdmin(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @GET("schools/template/download")
    suspend fun downloadTemplate(): Response<ResponseBody>

    @Multipart
    @POST
    suspend fun importSchools(@Part file: MultipartBody.Part): Response<ApiResponse<Unit>>

    @DELETE("schools/{id}")
    suspend fun deleteSchool(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @GET("school-admins")
    suspend fun getAllSchoolAdmins(): Response<ApiResponse<List<SchoolAdmin>>>

    @POST("school-admins")
    suspend fun addSchoolAdmin(@Body request: AdminCreateRequest): Response<ApiResponse<SchoolAdmin>>

    @POST("school-admins/assign")
    suspend fun assignAdmin(@Body request: AdminAssignRequest): Response<ApiResponse<Unit>>

    @POST("school-admin/unassign")
    suspend fun unassignAdmin(@Body request: AdminUnassignRequest): Response<ApiResponse<Unit>>

    @POST("school-admins/reset-password")
    suspend fun resetPassword(@Body request: AdminResetRequest): Response<ApiResponse<Unit>>

    @POST("school-admins/block")
    suspend fun blockAdmin(@Body request: AdminBlockRequest): Response<ApiResponse<Unit>>

}
