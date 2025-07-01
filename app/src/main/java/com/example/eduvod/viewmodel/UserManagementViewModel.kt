package com.example.eduvod.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduvod.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminUser(
    val email: String,
    val isBlocked: Boolean = false
)

data class AdminEduvodCreateRequest(
    val email: String,
    val password: String
)

data class AdminEduvodResetRequest(
    val email: String
)

//OG
class UserManagementViewModel : ViewModel() {

    val admins = mutableStateListOf(
        AdminUser("admin@eduvod.com"),
        AdminUser("alex@eduvod.com"),
        AdminUser("blair@eduvod.com"),
        AdminUser("dylan@eduvod.com"),
        AdminUser("patrick@eduvod.com"),
        AdminUser("jane@eduvod.com"),
        AdminUser("karey@eduvod.com"),
        AdminUser("shawn@eduvod.com"),
        AdminUser("derrick@eduvod.com"),

        )

    fun addAdmin(email: String): Boolean {
        if (admins.any { it.email.equals(email, ignoreCase = true) }) return false
        admins.add(AdminUser(email))
        return true
    }

    fun toggleBlock(admin: AdminUser) {
        val index = admins.indexOfFirst { it.email == admin.email }
        if (index != -1) {
            val updated = admin.copy(isBlocked = !admin.isBlocked)
            admins[index] = updated
        }
    }
}

//Retrofit
//class UserManagementViewModel(
//    private val repository: UserRepository = UserRepository()
//) : ViewModel() {
//    val admins = mutableStateListOf<AdminUser>()
//
//    private val _snackbarMessage = MutableStateFlow<String?>(null)
//    val snackbarMessage: StateFlow<String?> = _snackbarMessage
//
//    init {
//        fetchAllAdmins()
//    }
//
//    fun fetchAllAdmins() {
//        viewModelScope.launch {
//            try {
//                val response = repository.getAllEduvodAdmins()
//                if (response.isSuccessful) {
//                    response.body()?.data?.let {
//                        admins.clear()
//                        admins.addAll(it)
//                    }
//                }else {
//                    val error = response.errorBody()?.string()
//                    Log.e("UserVM", "Failed to fetch admins: $error")
//                    _snackbarMessage.value = "Failed to load admins."
//                }
//            } catch (e: Exception) {
//                Log.e("UserVM", "Error fetching admins: ${e.message}", e)
//                _snackbarMessage.value = "Network error: Could not load admins."
//            }
//        }
//    }
//
//    fun addAdmin(email: String, password: String) {
//        viewModelScope.launch {
//            try {
//                val response = repository.addEduvodAdmins(AdminEduvodCreateRequest(email, password))
//                if (response.isSuccessful) {
//                    fetchAllAdmins()
//                } else {
//                    val error = response.errorBody()?.string()
//                    Log.e("UserVM", "Add admin failed: $error")
//                    _snackbarMessage.value = "Failed to add admin."
//                }
//            } catch (e: Exception) {
//                Log.e("UserVM", "Error adding admin: ${e.message}", e)
//                _snackbarMessage.value = "Error occurred while adding admin."
//            }
//        }
//    }
//
//    fun toggleBlock(admin: AdminUser) {
//        viewModelScope.launch {
//            try {
//                val request = AdminBlockRequest(email = admin.email, block = !admin.isBlocked)
//                val response = repository.blockEduvodAdmin(request)
//                if (response.isSuccessful) {
//                    fetchAllAdmins()
//                } else {
//                    val error = response.errorBody()?.string()
//                    Log.e("UserVM", "Block/unblock failed: $error")
//                    _snackbarMessage.value = "Failed to update admin status."
//                }
//            } catch (e: Exception) {
//                Log.e("UserVM", "Error blocking/unblocking: ${e.message}", e)
//                _snackbarMessage.value = "Error updating admin block status."
//            }
//        }
//    }
//
//    fun resetPassword(email: String) {
//        viewModelScope.launch {
//            try {
//                val response = repository.resetEduvodAdmin(AdminEduvodResetRequest(email))
//                if (response.isSuccessful) {
//                    _snackbarMessage.value = "Password reset link sent."
//                } else {
//                    val error = response.errorBody()?.string()
//                    Log.e("UserVM", "Password reset failed: $error")
//                    _snackbarMessage.value = "Failed to send reset link."
//                }
//            } catch (e: Exception) {
//                Log.e("UserVM", "Error resetting password: ${e.message}", e)
//                _snackbarMessage.value = "Error resetting password."
//            }
//        }
//    }
//
//    fun clearSnackbar(){
//        _snackbarMessage.value = null
//    }
//}
























