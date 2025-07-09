package com.example.eduvod.repositories

import com.example.eduvod.model.CountyRequest
import com.example.eduvod.model.CountyResponse
import com.example.eduvod.model.RegionResponse
import com.example.eduvod.model.SimpleItem
import com.example.eduvod.model.SimpleNameRequest
import com.example.eduvod.model.SubCountyRequest
import com.example.eduvod.model.SubCountyResponse
import com.example.eduvod.retrofit.ApiService
import com.example.eduvod.retrofit.response.ApiResponse
import retrofit2.Response
import javax.inject.Inject

class SystemRepository @Inject constructor(
    private val api: ApiService
) {

    // --- School Types ---
    suspend fun getSchoolTypes(): Response<ApiResponse<List<SimpleItem>>> =
        api.getSchoolTypes()

    suspend fun addSchoolType(name: String): Response<SimpleItem> =
        api.addSchoolType(SimpleNameRequest(name))


    // --- School Categories ---
    suspend fun getSchoolCategories(): Response<ApiResponse<List<SimpleItem>>> =
        api.getSchoolCategories()

    suspend fun addSchoolCategory(name: String): Response<SimpleItem> =
        api.addSchoolCategory(SimpleNameRequest(name))


    // --- Curriculums ---
    suspend fun getCurriculums(): Response<ApiResponse<List<SimpleItem>>> =
        api.getCurriculums()

    suspend fun addCurriculum(name: String): Response<SimpleItem> =
        api.addCurriculum(SimpleNameRequest(name))


    // --- Regions ---
    suspend fun getRegions(): Response<ApiResponse<List<RegionResponse>>> =
        api.getRegions()

    suspend fun addRegion(name: String): Response<ApiResponse<RegionResponse>> =
        api.addRegion(SimpleNameRequest(name))

    suspend fun updateRegion(id: Long, name: String): Response<ApiResponse<RegionResponse>> =
        api.updateRegion(id, SimpleNameRequest(name))

    suspend fun deleteRegion(id: Long): Response<ApiResponse<String>> =
        api.deleteRegion(id)


    // --- Counties ---
    suspend fun getCounties(): Response<ApiResponse<List<CountyResponse>>> =
        api.getCounties()

    suspend fun getCountiesByRegion(regionId: Long): Response<ApiResponse<List<CountyResponse>>> =
        api.getCountiesByRegion(regionId)

    suspend fun addCounty(name: String, regionId: Long): Response<ApiResponse<CountyResponse>> =
        api.addCounty(CountyRequest(name, regionId))

    suspend fun updateCounty(id: Long, name: String, regionId: Long): Response<ApiResponse<CountyResponse>> =
        api.updateCounty(id, CountyRequest(name, regionId))

    suspend fun deleteCounty(id: Long): Response<ApiResponse<String>> =
        api.deleteCounty(id)


    // --- SubCounties ---
    suspend fun getSubCounties(): Response<ApiResponse<List<SubCountyResponse>>> =
        api.getSubCounties()

    suspend fun getSubCountiesByCounty(countyId: Long): Response<ApiResponse<List<SubCountyResponse>>> =
        api.getSubCountiesByCounty(countyId)

    suspend fun addSubCounty(name: String, countyId: Long): Response<ApiResponse<SubCountyResponse>> =
        api.addSubCounty(SubCountyRequest(name, countyId))

    suspend fun updateSubCounty(id: Long, name: String, countyId: Long): Response<ApiResponse<SubCountyResponse>> =
        api.updateSubCounty(id, SubCountyRequest(name, countyId))

    suspend fun deleteSubCounty(id: Long): Response<ApiResponse<String>> =
        api.deleteSubCounty(id)
}
