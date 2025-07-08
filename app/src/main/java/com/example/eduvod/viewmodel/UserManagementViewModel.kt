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
    val id: Long,
    val username: String,
    val email: String,
    val role: String,
    val schoolName: String?,
    val status: String
)

data class AdminEduvodCreateRequest(
    val email: String,
    val password: String
)
data class AdminEduvodResetRequest(
    val email: String
)
data class AdminEduvodBlockRequest(
    val email: String,
    val block: Boolean
)

//OG
//class UserManagementViewModel : ViewModel() {
//
//    val admins = mutableStateListOf(
//        AdminUser("admin@eduvod.com"),
//        AdminUser("alex@eduvod.com"),
//        AdminUser("blair@eduvod.com"),
//        AdminUser("dylan@eduvod.com"),
//        AdminUser("patrick@eduvod.com"),
//        AdminUser("jane@eduvod.com"),
//        AdminUser("karey@eduvod.com"),
//        AdminUser("shawn@eduvod.com"),
//        AdminUser("derrick@eduvod.com"),
//    )
//
//    private val _snackbarMessage = MutableStateFlow<String?>(null)
//    val snackbarMessage: StateFlow<String?> = _snackbarMessage
//
//    fun fetchAllAdmins() {
//        Log.d("UserVM", "Dummy: Fetched all admins.")
//        _snackbarMessage.value = "Admins loaded"
//    }
//
//    fun addAdmin(email: String, password: String) {
//        viewModelScope.launch {
//            val exists = admins.any { it.email.equals(email, ignoreCase = true) }
//            if (exists) {
//                _snackbarMessage.value = "Admin already exists"
//                return@launch
//            }
//
//            admins.add(AdminUser(email))
//            Log.d("UserVM", "Dummy: Added admin $email")
//            _snackbarMessage.value = "Admin added successfully"
//        }
//    }
//
//    fun toggleBlock(admin: AdminUser) {
//        viewModelScope.launch {
//            val index = admins.indexOfFirst { it.email == admin.email }
//            if (index != -1) {
//                admins[index] = admin.copy(isBlocked = !admin.isBlocked)
//                _snackbarMessage.value = if (admin.isBlocked) {
//                    "Admin unblocked"
//                } else {
//                    "Admin blocked"
//                }
//                Log.d("UserVM", "Dummy: Toggled block for ${admin.email}")
//            }
//        }
//    }
//
//    fun resetPassword(email: String) {
//        viewModelScope.launch {
//            val exists = admins.any { it.email == email }
//            if (exists) {
//                _snackbarMessage.value = "Password reset link sent"
//                Log.d("UserVM", "Dummy: Password reset for $email")
//            } else {
//                _snackbarMessage.value = "Admin not found"
//            }
//        }
//    }
//
//    fun clearSnackbar() {
//        _snackbarMessage.value = null
//    }
//}

//Retrofit
class UserManagementViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    val admins = mutableStateListOf<AdminUser>()
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    init {
        fetchAllAdmins()
    }

    fun fetchAllAdmins() {
        viewModelScope.launch {
            try {
                val response = repository.getAllUsers()
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        admins.clear()
                        admins.addAll(it)
                    }
                } else {
                    _snackbarMessage.value = "Failed to load admins."
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Network error."
            }
        }
    }

    fun registerSuperAdmin(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.registerSuperAdmin(AdminEduvodCreateRequest(email, password))
                if (response.isSuccessful) {
                    fetchAllAdmins()
                    _snackbarMessage.value = "Super Admin registered."
                } else {
                    _snackbarMessage.value = "Failed to register."
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error occurred during registration."
            }
        }
    }

    fun toggleUserStatus(admin: AdminUser) {
        viewModelScope.launch {
            val newStatus = when (admin.status) {
                "ACTIVE" -> "BLOCKED"
                "BLOCKED" -> "ACTIVE"
                else -> "ACTIVE"
            }
            try {
                val response = repository.updateUserStatus(admin.id, newStatus)
                if (response.isSuccessful) {
                    fetchAllAdmins()
                } else {
                    _snackbarMessage.value = "Failed to update status."
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error updating status."
            }
        }
    }

    fun deleteUser(id: Long) {
        viewModelScope.launch {
            try {
                val response = repository.deleteUser(id)
                if (response.isSuccessful) {
                    fetchAllAdmins()
                    _snackbarMessage.value = "User deleted."
                } else {
                    _snackbarMessage.value = "Failed to delete user."
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error deleting user."
            }
        }
    }
    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                val response = repository.resetEduvodAdmin(AdminEduvodResetRequest(email))
                if (response.isSuccessful) {
                    _snackbarMessage.value = "Password reset link sent."
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("UserVM", "Password reset failed: $error")
                    _snackbarMessage.value = "Failed to send reset link."
                }
            } catch (e: Exception) {
                Log.e("UserVM", "Error resetting password: ${e.message}", e)
                _snackbarMessage.value = "Error resetting password."
            }
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
























