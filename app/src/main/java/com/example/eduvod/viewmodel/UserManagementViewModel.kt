package com.example.eduvod.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.eduvod.ui.screens.AdminUser

class UserManagementViewModel : ViewModel() {

    val admins = mutableStateListOf(
        AdminUser("admin@eduvod.com"),
        AdminUser("alex@eduvod.com")
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
