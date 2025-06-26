package com.example.eduvod.repositories

import com.example.eduvod.retrofit.ApiClient

class SystemRepository {

    suspend fun fetchSystemConfig(section: String): List<String> {
        return try {
            val response = ApiClient.apiService.getConfigSection(section)
            if (response.isSuccessful) response.body()?.data ?: emptyList()
            else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addSystemConfig(section: String, value: String): Boolean {
        return try {
            val response = ApiClient.apiService.addConfigItem(section, mapOf("value" to value))
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateSystemConfig(section: String, oldValue: String, newValue: String): Boolean {
        return try {
            val response = ApiClient.apiService.updateConfigItem(
                section, mapOf("oldValue" to oldValue, "newValue" to newValue)
            )
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteSystemConfig(section: String, value: String): Boolean {
        return try {
            val response = ApiClient.apiService.deleteConfigItem(section, mapOf("value" to value))
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}