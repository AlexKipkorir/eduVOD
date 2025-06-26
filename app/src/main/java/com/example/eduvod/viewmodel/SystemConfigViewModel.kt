package com.example.eduvod.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.window.isPopupLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduvod.repositories.SystemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

//OG
//class SystemConfigViewModel : ViewModel() {
//
//    // Lists representing dropdown data
//    val types = mutableStateListOf("Primary", "Secondary", "Mixed")
//    val categories = mutableStateListOf("Public", "Private")
//    val curriculums = mutableStateListOf("CBC", "8-4-4", "British", "IGSE")
//    val regions = mutableStateListOf("Nairobi Diocese", "Mombasa Diocese", "Kisumu Diocese", "Eldoret Diocese", "Garissa Diocese", "Isiolo Diocese", "Nakuru Diocese", "Turkana Diocese",)
//
//    fun addItem(section: String, value: String) { sectionList(section).add(value) }
//
//    fun updateItem(section: String, old: String, new: String) {
//        val list = sectionList(section)
//        val index = list.indexOf(old)
//        if (index != -1) list[index] = new
//    }
//
//    fun deleteItem(section: String, value: String) {
//        sectionList(section).remove(value)
//    }
//
//    fun sectionList(section: String): SnapshotStateList<String> = when (section) {
//        "School Type" -> types
//        "School Category" -> categories
//        "Curriculum" -> curriculums
//        "Region / Diocese" -> regions
//        else -> mutableStateListOf()
//    }
//
//}

//Retrofit
class SystemConfigViewModel(
    private val repository: SystemRepository = SystemRepository()
) : ViewModel() {

    val types = mutableStateListOf<String>()
    val categories = mutableStateListOf<String>()
    val curriculums = mutableStateListOf<String>()
    val regions = mutableStateListOf<String>()

    val snackbarMessage = MutableStateFlow<String?>(null)

    init {
        loadAll()
    }

    fun sectionList(section: String): MutableList<String> {
        return when (section) {
            "School Type" -> types
            "School Category" -> categories
            "Curriculum" -> curriculums
            "Region / Diocese" -> regions
            else -> mutableStateListOf()
        }
    }

    private fun loadAll() {
        loadConfig("School Type", types)
        loadConfig("School Category", categories)
        loadConfig("Curriculum", curriculums)
        loadConfig("Region / Diocese", regions)
    }

    private fun loadConfig(section: String, list: MutableList<String>) {
        viewModelScope.launch {
            try {
                val fetched = repository.fetchSystemConfig(section)
                list.clear()
                list.addAll(fetched)
            } catch (e: Exception) {
                snackbarMessage.value = "Failed to load $section: ${e.localizedMessage}"
            }
        }
    }

    fun addItem(section: String, value: String) {
        val list = sectionList(section)
        viewModelScope.launch {
            try {
                val success = repository.addSystemConfig(section, value)
                if (success) {
                    list.add(value)
                } else {
                    snackbarMessage.value = "Failed to add item to $section"
                }
            } catch (e: Exception) {
                snackbarMessage.value = "Error adding to $section: ${e.localizedMessage}"
            }
        }
    }

    fun updateItem(section: String, oldValue: String, newValue: String) {
        val list = sectionList(section)
        viewModelScope.launch {
            try {
                val success = repository.updateSystemConfig(section, oldValue, newValue)
                if (success) {
                    val index = list.indexOf(oldValue)
                    if (index != -1) {
                        list[index] = newValue
                    }
                } else {
                    snackbarMessage.value = "Failed to update $section"
                }
            } catch (e: Exception) {
                snackbarMessage.value = "Error updating $section: ${e.localizedMessage}"
            }
        }
    }

    fun deleteItem(section: String, value: String) {
        val list = sectionList(section)
        viewModelScope.launch {
            try {
                val success = repository.deleteSystemConfig(section, value)
                if (success) {
                    list.remove(value)
                } else {
                    snackbarMessage.value = "Failed to delete item from $section"
                }
            } catch (e: Exception) {
                snackbarMessage.value = "Error deleting from $section: ${e.localizedMessage}"
            }
        }
    }

    fun clearSnackbar() {
        snackbarMessage.value = null
    }

}