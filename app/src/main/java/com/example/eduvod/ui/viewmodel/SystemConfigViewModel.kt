package com.example.eduvod.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel


class SystemConfigViewModel : ViewModel() {

    // Lists representing dropdown data
    var types = mutableStateListOf("Primary", "Secondary")
        private set

    var categories = mutableStateListOf("Public", "Private")
        private set

    var curriculums = mutableStateListOf("CBC", "British", "IGSE")
        private set

    //CRUD Operations

    fun addItem(section: String, item: String) {
        when (section) {
            "School Type" -> types.add(item)
            "School Category" -> categories.add(item)
            "Curriculum" -> curriculums.add(item)
        }
    }

    fun deleteItem(section: String, item: String) {
        when(section) {
            "School Type" -> types.remove(item)
            "School Category" -> categories.remove(item)
            "Curriculum" -> curriculums.remove(item)
        }
    }

    fun updateItem(section: String, oldItem: String, newItem: String) {
        val list = when (section) {
            "School Type" -> types
            "School Category" -> categories
            "Curriculum" -> curriculums
            else -> null
        }
        list?.let {
            val index = it.indexOf(oldItem)
            if (index != -1) {
                it[index] = newItem
            }
        }
    }
}