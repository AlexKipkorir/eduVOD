package com.example.eduvod.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduvod.model.SuperAdminDashboardResponse
import com.example.eduvod.repositories.DashboardRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class DashboardViewModel(
    private val repository: DashboardRepository = DashboardRepository()
) : ViewModel() {

    private val _stats = MutableStateFlow<SuperAdminDashboardResponse?>(null)
    open val stats: StateFlow<SuperAdminDashboardResponse?> = _stats

    private val _snackbar = MutableStateFlow<String?>(null)
    val snackbar: StateFlow<String?> = _snackbar
    private val _isLoading = MutableStateFlow(true)
    open val isLoading: StateFlow<Boolean> = _isLoading


    init {
        fetchDashboardStats()
    }

    fun fetchDashboardStats() {
        Log.d("DashboardViewModel", "fetchDashboardStats: Called")

        viewModelScope.launch {
            _isLoading.value = true
            Log.d("DashboardViewModel", "Loading started")

            delay(1000)

            try {
                val result = repository.getDashboardStats()
                Log.d("DashboardViewModel", "API call completed")

                result.onSuccess { data ->
                    Log.d("DashboardViewModel", "Data fetched successfully: $data")
                    _stats.value = data
                }.onFailure { e ->
                    Log.e("DashboardViewModel", "Error fetching data: ${e.localizedMessage}", e)
                    _snackbar.value = e.localizedMessage ?: "Unknown error fetching dashboard stats"
                }
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Exception during fetch: ${e.localizedMessage}", e)
                _snackbar.value = "Exception occurred: ${e.localizedMessage}"
            }

            _isLoading.value = false
            Log.d("DashboardViewModel", "Loading finished")
        }
    }
    fun clearSnackbar() {
        _snackbar.value = null
    }
}
data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)




//OG
//class DashboardViewModel : ViewModel() {
//
//    var schoolsByRegion by mutableStateOf(
//        listOf(
//            "Nairobi" to "45",
//            "Mombasa" to "30",
//            "Kisumu" to "22",
//            "Nakuru" to "18",
//            "Eldoret" to "10",
//            "Garissa" to "8",
//            "Isiolo" to "3",
//            "Turkana" to "1"
//
//        )
//    )
//
//    var studentsByGender by mutableStateOf(
//        mapOf(
//            "Male" to "12,300",
//            "Female" to "11,800"
//        )
//    )
//
//    var differentlyAbledStudents by mutableStateOf(
//        mapOf(
//            "Male" to "230",
//            "Female" to "210"
//        )
//    )
//
//    var teachersByGender by mutableStateOf(
//        mapOf(
//            "Male" to "2,500",
//            "Female" to "3,100"
//        )
//    )
//
//    var totalGuardian by mutableStateOf("8,000")
//
//    var studentsByClassStream by mutableStateOf(
//        listOf(
//            "Grade 1 - Stream A" to "300",
//            "Grade 2 - Stream B" to "280",
//            "Grade 3 - Stream A" to "295",
//            "Grade 4 - Stream C" to "310",
//            "Grade 5 - Stream A" to "290"
//        )
//    )
//}