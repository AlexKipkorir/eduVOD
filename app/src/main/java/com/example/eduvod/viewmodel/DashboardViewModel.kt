package com.example.eduvod.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduvod.repositories.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



data class DashboardStats(
    val schoolsByRegion: Map<String, Int>,
    val studentsByGender: Map<String, Int>,
    val differentlyAbled: Map<String, Int>,
    val teachersByGender: Map<String, Int>,
    val guardiansCount: Int,
    val studentsByClass: Map<String, Int>
)

//Retrofit
//
//class DashboardViewModel(
//    private val repository: DashboardRepository = DashboardRepository()
//) : ViewModel() {
//
//    private val _stats = MutableStateFlow<DashboardStats?>(null)
//    val stats: StateFlow<DashboardStats?> = _stats
//
//    private val _snackbar = MutableStateFlow<String?>(null)
//    val snackbar: StateFlow<String?> = _snackbar
//
//    init {
//        fetchStats()
//    }
//
//    private fun fetchStats() {
//        viewModelScope.launch {
//            try {
//                val response = repository.getDashboardStats()
//                if (response.isSuccessful) {
//                    _stats.value = response.body()?.data
//                } else {
//                    _snackbar.value = "Failed to fetch dashboard stats"
//                }
//            } catch (e: Exception) {
//                _snackbar.value = "Error fetching dashboard: ${e.localizedMessage}"
//            }
//        }
//    }
//
//    fun clearSnackbar() {
//        _snackbar.value = null
//    }
//}


//OG
class DashboardViewModel : ViewModel() {

    var schoolsByRegion by mutableStateOf(
        listOf(
            "Nairobi" to "45",
            "Mombasa" to "30",
            "Kisumu" to "22",
            "Nakuru" to "18",
            "Eldoret" to "10",
            "Garissa" to "8",
            "Isiolo" to "3",
            "Turkana" to "1"

        )
    )

    var studentsByGender by mutableStateOf(
        mapOf(
            "Male" to "12,300",
            "Female" to "11,800"
        )
    )

    var differentlyAbledStudents by mutableStateOf(
        mapOf(
            "Male" to "230",
            "Female" to "210"
        )
    )

    var teachersByGender by mutableStateOf(
        mapOf(
            "Male" to "2,500",
            "Female" to "3,100"
        )
    )

    var totalGuardian by mutableStateOf("8,000")

    var studentsByClassStream by mutableStateOf(
        listOf(
            "Grade 1 - Stream A" to "300",
            "Grade 2 - Stream B" to "280",
            "Grade 3 - Stream A" to "295",
            "Grade 4 - Stream C" to "310",
            "Grade 5 - Stream A" to "290"
        )
    )
}