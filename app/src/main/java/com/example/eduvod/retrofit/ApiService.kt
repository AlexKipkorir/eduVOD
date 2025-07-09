package com.example.eduvod.retrofit

import com.example.eduvod.model.CountyRequest
import com.example.eduvod.model.CountyResponse
import com.example.eduvod.model.Grade
import com.example.eduvod.model.GradeRequest
import com.example.eduvod.model.RegionResponse
import com.example.eduvod.model.RenameStreamRequest
import com.example.eduvod.model.School
import com.example.eduvod.model.SimpleItem
import com.example.eduvod.model.SimpleNameRequest
import com.example.eduvod.model.Stream
import com.example.eduvod.model.SubCountyRequest
import com.example.eduvod.model.SubCountyResponse
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
import com.example.eduvod.viewmodel.DashboardStats
import com.example.eduvod.viewmodel.SchoolAdmin
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    //Login
    @POST("api/v1/auth/superadmin/login")
    suspend fun loginSuperAdmin(@Body request: LoginRequest): Response<ApiResponse<LoginResponseData>>

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
    // Register new super admin
    @POST("/api/v1/auth/superadmin/register")
    suspend fun registerSuperAdmin(
        @Body request: AdminEduvodCreateRequest
    ): Response<ApiResponse<Unit>>

    // Fetch all users
    @GET("/api/v1/superadmin/users")
    suspend fun getAllUsers(): Response<ApiResponse<List<AdminUser>>>

    // Change user status (ACTIVE/BLOCKED/DELETED)
    @PUT("/api/v1/superadmin/users/{id}/status")
    suspend fun updateUserStatus(
        @Path("id") id: Long,
        @Query("status") status: String
    ): Response<ApiResponse<Unit>>

    // Soft delete user
    @DELETE("/api/v1/superadmin/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<ApiResponse<Unit>>

    @POST("admin-users/reset-password")
    suspend fun  resetEduvodAdmin(@Body request: AdminEduvodResetRequest): Response<ApiResponse<Unit>>

    //System Configuration
    // --- School Types ---
    @GET("/api/v1/superadmin/school-types")
    suspend fun getSchoolTypes(): Response<ApiResponse<List<SimpleItem>>>

    @POST("/api/v1/superadmin/school-types")
    suspend fun addSchoolType(@Body item: SimpleNameRequest): Response<SimpleItem>


    // --- School Categories ---
    @GET("/api/v1/superadmin/school-categories")
    suspend fun getSchoolCategories(): Response<ApiResponse<List<SimpleItem>>>

    @POST("/api/v1/superadmin/school-categories")
    suspend fun addSchoolCategory(@Body item: SimpleNameRequest): Response<SimpleItem>


    // --- Curriculums ---
    @GET("/api/v1/superadmin/curriculum")
    suspend fun getCurriculums(): Response<ApiResponse<List<SimpleItem>>>

    @POST("/api/v1/superadmin/curriculum")
    suspend fun addCurriculum(@Body item: SimpleNameRequest): Response<SimpleItem>


    // --- Regions ---
    @GET("/api/v1/superadmin/regions")
    suspend fun getRegions(): Response<ApiResponse<List<RegionResponse>>>

    @POST("/api/v1/superadmin/regions")
    suspend fun addRegion(@Body item: SimpleNameRequest): Response<ApiResponse<RegionResponse>>

    @PUT("/api/v1/superadmin/regions/{id}")
    suspend fun updateRegion(@Path("id") id: Long, @Body item: SimpleNameRequest): Response<ApiResponse<RegionResponse>>

    @DELETE("/api/v1/superadmin/regions/{id}")
    suspend fun deleteRegion(@Path("id") id: Long): Response<ApiResponse<String>>


    // --- Counties ---
    @GET("/api/v1/superadmin/counties")
    suspend fun getCounties(): Response<ApiResponse<List<CountyResponse>>>

    @GET("/api/v1/superadmin/regions/{regionId}/counties")
    suspend fun getCountiesByRegion(@Path("regionId") regionId: Long): Response<ApiResponse<List<CountyResponse>>>

    @POST("/api/v1/superadmin/counties")
    suspend fun addCounty(@Body item: CountyRequest): Response<ApiResponse<CountyResponse>>

    @PUT("/api/v1/superadmin/counties/{id}")
    suspend fun updateCounty(@Path("id") id: Long, @Body item: CountyRequest): Response<ApiResponse<CountyResponse>>

    @DELETE("/api/v1/superadmin/counties/{id}")
    suspend fun deleteCounty(@Path("id") id: Long): Response<ApiResponse<String>>


    // --- SubCounties ---
    @GET("/api/v1/superadmin/subcounties")
    suspend fun getSubCounties(): Response<ApiResponse<List<SubCountyResponse>>>

    @GET("/api/v1/superadmin/counties/{countyId}/subcounties")
    suspend fun getSubCountiesByCounty(@Path("countyId") countyId: Long): Response<ApiResponse<List<SubCountyResponse>>>

    @POST("/api/v1/superadmin/subcounties")
    suspend fun addSubCounty(@Body item: SubCountyRequest): Response<ApiResponse<SubCountyResponse>>

    @PUT("/api/v1/superadmin/subcounties/{id}")
    suspend fun updateSubCounty(@Path("id") id: Long, @Body item: SubCountyRequest): Response<ApiResponse<SubCountyResponse>>

    @DELETE("/api/v1/superadmin/subcounties/{id}")
    suspend fun deleteSubCounty(@Path("id") id: Long): Response<ApiResponse<String>>

    //Grades Management
    @GET("/api/v1/superadmin/grades")
    suspend fun getAllGrades(): Response<ApiResponse<List<Grade>>>

    @POST("/api/v1/superadmin/grades")
    suspend fun addGrade(@Body request: GradeRequest): Response<ApiResponse<Grade>>

    @DELETE("/api/v1/superadmin/grades/{id}")
    suspend fun deleteGrade(@Path("id") id: Int): Response<ApiResponse<String>>

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

    //Dashboard
    @GET("dashboard")
    suspend fun getDashboardStats(): Response<ApiResponse<DashboardStats>>
}
