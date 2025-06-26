package com.example.eduvod.retrofit

import com.example.eduvod.model.Grade
import com.example.eduvod.model.RenameStreamRequest
import com.example.eduvod.model.School
import com.example.eduvod.model.Stream
import com.example.eduvod.retrofit.request.LoginRequest
import com.example.eduvod.retrofit.response.ApiResponse
import com.example.eduvod.retrofit.response.LoginResponseData
import com.example.eduvod.retrofit.response.SchoolResponse
import com.example.eduvod.viewmodel.AdminUser
import com.example.eduvod.viewmodel.AdminAssignRequest
import com.example.eduvod.viewmodel.AdminBlockRequest
import com.example.eduvod.viewmodel.AdminCreateRequest
import com.example.eduvod.viewmodel.AdminEduvodCreateRequest
import com.example.eduvod.viewmodel.AdminEduvodResetRequest
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

    //User Management
    @GET("admin-users")
    suspend fun getAllEduvodAdmins(): Response<ApiResponse<List<AdminUser>>>

    @POST("admin-users")
    suspend fun addEduvodAdmin(@Body request: AdminEduvodCreateRequest): Response<ApiResponse<Unit>>

    @POST("admin-user/block")
    suspend fun blockEduvodAdmin(@Body request: AdminBlockRequest): Response<ApiResponse<Unit>>

    @POST("admin-users/reset-password")
    suspend fun  resetEduvodAdmin(@Body request: AdminEduvodResetRequest): Response<ApiResponse<Unit>>

    //System Configuration
    @GET("systems-config/{section}")
    suspend fun getConfigSection(@Path("section") section: String): Response<ApiResponse<List<String>>>

    @POST("system-config/{section}")
    suspend fun addConfigItem(
        @Path("section") section: String,
        @Body item: Map<String, String>
    ): Response<ApiResponse<Unit>>

    @PUT("system-config/{section}")
    suspend fun updateConfigItem(
        @Path("section") section: String,
        @Body body: Map<String, String>
    ): Response<ApiResponse<Unit>>

    @HTTP(method = "DELETE", path = "system-config/{section}", hasBody = true)
    suspend fun deleteConfigItem(
        @Path("section") section: String,
        @Body item: Map<String, String>
    ): Response<ApiResponse<Unit>>

    //Grades Management
    @GET("grades")
    suspend fun getAllGrades(): Response<ApiResponse<List<Grade>>>

    @POST("grades")
    suspend fun addGrade(@Body grade: Grade): Response<ApiResponse<Grade>>

    @DELETE("grades/{name}")
    suspend fun deleteGrade(@Path("name") name: String): Response<ApiResponse<Unit>>

    //Stream Management
    @POST("grades/{gradeName}/streams")
    suspend fun addStreamToGrade(
        @Path("gradeName") gradeName: String,
        @Body stream: Stream
    ): Response<ApiResponse<Unit>>

    @PUT("grades/{gradeName}/streams")
    suspend fun renameStream(
        @Path("gradeName") gradeName: String,
        @Body renameRequest: RenameStreamRequest
    ): Response<ApiResponse<Unit>>

    @DELETE("grades/{gradeName}/streams/{streamName}")
    suspend fun deleteStream(
        @Path("gradeName") gradeName: String,
        @Path("streamName") streamName: String
    ): Response<ApiResponse<Unit>>



}
