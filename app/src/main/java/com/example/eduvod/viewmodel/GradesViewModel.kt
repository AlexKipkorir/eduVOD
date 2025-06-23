package com.example.eduvod.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.eduvod.model.Grade
import com.example.eduvod.model.Stream

class GradesViewModel : ViewModel() {
    val grades = mutableStateListOf(
        Grade("Grade 1", "CBC", hasSchool = false, streams = mutableStateListOf(
            Stream("North"), Stream("South"), Stream("East"), Stream("West")
        )),
        Grade("Grade 2", "CBC", hasSchool = true, streams = mutableStateListOf(
            Stream("North"), Stream("South"), Stream("East"), Stream("West")
        )),
        Grade("Form 1", "8-4-4", hasSchool = false, streams = mutableStateListOf(
            Stream("A"), Stream("B")
        ))
    )
    val allCurriculums = listOf("CBC","8-4-4","IGCSE","British")

    val selectedGrade = mutableStateOf<Grade?>(null)
    val newStreamName = mutableStateOf("")

    fun viewStreams(grade: Grade) {
        selectedGrade.value = grade
    }
    fun addStreamToSelectedGrade(stream: String) {
        selectedGrade.value?.let {
            if (!it.streams.any { s -> s.name == stream }) {
                it.streams.add(Stream(stream))
            }
        }
        newStreamName.value = ""
    }
    fun removeStreamFromSelectedGrade(stream: String) {
        selectedGrade.value?.streams?.removeIf { it.name == stream }
    }

    fun addGrade(grade: Grade) {
        grades.add(grade)
    }
    fun deleteGrade(grade: Grade) {
        grades.remove(grade)
    }
    fun renameStream(gradeName: String, oldName: String, newName: String): Boolean {
        val grade = grades.find { it.name == gradeName } ?: return false

        if (grade.streams.any { it.name.equals(newName, ignoreCase = true) && it.name != oldName }) {
            return false
        }

        val index = grade.streams.indexOfFirst { it.name == oldName }
        if (index != -1) {
            grade.streams[index] = grade.streams[index].copy(name = newName)
            return true
        }
        return false
    }


}
