package com.example.eduvod.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel


class SystemConfigViewModel : ViewModel() {

    // Lists representing dropdown data
    val types = mutableStateListOf("Primary", "Secondary", "Mixed")
    val categories = mutableStateListOf("Public", "Private")
    val curriculums = mutableStateListOf("CBC", "8-4-4", "British", "IGSE")
    val regions = mutableStateListOf("Nairobi Diocese", "Mombasa Diocese", "Kisumu Diocese", "Eldoret Diocese", "Garissa Diocese", "Isiolo Diocese", "Nakuru Diocese", "Turkana Diocese",)

    fun addItem(section: String, value: String) { sectionList(section).add(value) }

    fun updateItem(section: String, old: String, new: String) {
        val list = sectionList(section)
        val index = list.indexOf(old)
        if (index != -1) list[index] = new
    }

    fun deleteItem(section: String, value: String) {
        sectionList(section).remove(value)
    }

    fun sectionList(section: String): SnapshotStateList<String> = when (section) {
        "School Type" -> types
        "School Category" -> categories
        "Curriculum" -> curriculums
        "Region / Diocese" -> regions
        else -> mutableStateListOf()
    }

}