package com.example.eduvod.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduvod.model.Grade
import com.example.eduvod.model.Stream
import com.example.eduvod.repositories.GradesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//OG
class GradesViewModel : ViewModel() {
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    val grades = mutableStateListOf(
        Grade("Grade 1", "CBC", hasSchool = false, streams = mutableStateListOf(Stream("North"), Stream("South"))),
        Grade("Grade 2", "CBC", hasSchool = true, streams = mutableStateListOf(Stream("East"), Stream("West"))),
        Grade("Form 1", "8-4-4", hasSchool = false, streams = mutableStateListOf(Stream("A"), Stream("B"))),
        Grade("Form 2", "8-4-4", hasSchool = true, streams = mutableStateListOf(Stream("C"))),
        Grade("Year 7", "British", hasSchool = false, streams = mutableStateListOf(Stream("Alpha"), Stream("Beta"))),
        Grade("Year 8", "British", hasSchool = true, streams = mutableStateListOf(Stream("Gamma"))),
        Grade("IGCSE 1", "IGCSE", hasSchool = false, streams = mutableStateListOf(Stream("Red"), Stream("Blue"))),
        Grade("IGCSE 2", "IGCSE", hasSchool = true, streams = mutableStateListOf(Stream("Green")))
    )

    val selectedGrade = mutableStateOf<Grade?>(null)
    val allCurriculums = listOf("CBC", "British", "IGCSE", "8-4-4")

    init {
        fetchGrades()
    }

    fun fetchGrades(){

    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun viewStreams(grade: Grade) {
        selectedGrade.value = grade
    }

    fun addGrade(grade: Grade) {
        grades.add(grade)
        _snackbarMessage.value = "Grade added successfully"
    }

    fun deleteGrade(grade: Grade) {
        grades.remove(grade)
        _snackbarMessage.value = "Grade deleted successfully"
    }

    fun addStreamToSelectedGrade(streamName: String) {
        val grade = selectedGrade.value ?: return
        if (grade.streams.any { it.name.equals(streamName, ignoreCase = true) }) {
            _snackbarMessage.value = "Stream already exists"
            return
        }

        grade.streams.add(Stream(streamName))
        _snackbarMessage.value = "Stream added"
    }

    fun removeStreamFromSelectedGrade(streamName: String) {
        val grade = selectedGrade.value ?: return
        grade.streams.removeIf { it.name == streamName }
        _snackbarMessage.value = "Stream deleted"
    }

    fun renameStream(gradeName: String, oldName: String, newName: String): Boolean {
        val grade = selectedGrade.value ?: return false

        if (grade.streams.any { it.name.equals(newName, ignoreCase = true) && it.name != oldName }) {
            _snackbarMessage.value = "Stream name already exists"
            return false
        }

        val index = grade.streams.indexOfFirst { it.name == oldName }
        if (index != -1) {
            grade.streams[index] = grade.streams[index].copy(name = newName)
            _snackbarMessage.value = "Stream renamed"
            return true
        }

        _snackbarMessage.value = "Rename failed"
        return false
    }

    fun moveStreamUp(index: Int) {
        val grade = selectedGrade.value ?: return
        if (index > 0 && index < grade.streams.size) {
            val temp = grade.streams[index]
            grade.streams[index] = grade.streams[index - 1]
            grade.streams[index - 1] = temp
        }
    }

    fun moveStreamDown(index: Int) {
        val grade = selectedGrade.value ?: return
        if (index >= 0 && index < grade.streams.size - 1) {
            val temp = grade.streams[index]
            grade.streams[index] = grade.streams[index + 1]
            grade.streams[index + 1] = temp
        }
    }
}

//Retrofit
//class GradesViewModel(
//    private val repository: GradesRepository = GradesRepository()
//) : ViewModel() {
//
//    val grades = mutableStateListOf<Grade>()
//    val selectedGrade = mutableStateOf<Grade?>(null)
//
//    val allCurriculums = listOf("CBC", "British", "IGCSE", "8-4-4")
//
//    private val _snackbarMessage = MutableStateFlow<String?>(null)
//    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()
//
//    init {
//        fetchGrades()
//    }
//
//    fun clearSnackbar() {
//        _snackbarMessage.value = null
//    }
//
//    fun fetchGrades() {
//        viewModelScope.launch {
//            try {
//                val response = repository.getGrades()
//                if (response.isSuccessful) {
//                    response.body()?.data?.let {
//                        grades.clear()
//                        grades.addAll(it)
//                    }
//                } else {
//                    _snackbarMessage.value = "Failed to fetch grades"
//                }
//            } catch (e: Exception) {
//                _snackbarMessage.value = "Error fetching grades: ${e.localizedMessage}"
//            }
//        }
//    }
//
//    fun addGrade(grade: Grade) {
//        viewModelScope.launch {
//            try {
//                val response = repository.addGrade(grade)
//                if (response.isSuccessful) {
//                    response.body()?.data?.let {
//                        grades.add(it)
//                        _snackbarMessage.value = "Grade added successfully"
//                    }
//                } else {
//                    _snackbarMessage.value = "Failed to add grade"
//                }
//            } catch (e: Exception) {
//                _snackbarMessage.value = "Error adding grade: ${e.localizedMessage}"
//            }
//        }
//    }
//
//    fun deleteGrade(grade: Grade) {
//        viewModelScope.launch {
//            try {
//                val response = repository.deleteGrade(grade.name)
//                if (response.isSuccessful) {
//                    grades.remove(grade)
//                    _snackbarMessage.value = "Grade deleted successfully"
//                } else {
//                    _snackbarMessage.value = "Failed to delete grade"
//                }
//            } catch (e: Exception) {
//                _snackbarMessage.value = "Error deleting grade: ${e.localizedMessage}"
//            }
//        }
//    }
//
//    fun addStreamToSelectedGrade(streamName: String) {
//        val grade = selectedGrade.value ?: return
//        viewModelScope.launch {
//            if (grade.streams.any { it.name.equals(streamName, ignoreCase = true) }) {
//                _snackbarMessage.value = "Stream already exists"
//                return@launch
//            }
//
//            try {
//                val response = repository.addStreamToGrade(grade.name, Stream(streamName))
//                if (response.isSuccessful) {
//                    grade.streams.add(Stream(streamName))
//                    _snackbarMessage.value = "Stream added"
//                } else {
//                    _snackbarMessage.value = "Failed to add stream"
//                }
//            } catch (e: Exception) {
//                _snackbarMessage.value = "Error adding stream: ${e.localizedMessage}"
//            }
//        }
//    }
//
//    fun removeStreamFromSelectedGrade(streamName: String) {
//        val grade = selectedGrade.value ?: return
//        viewModelScope.launch {
//            try {
//                val response = repository.deleteStream(grade.name, streamName)
//                if (response.isSuccessful) {
//                    grade.streams.removeIf { it.name == streamName }
//                    _snackbarMessage.value = "Stream deleted"
//                } else {
//                    _snackbarMessage.value = "Failed to delete stream"
//                }
//            } catch (e: Exception) {
//                _snackbarMessage.value = "Error deleting stream: ${e.localizedMessage}"
//            }
//        }
//    }
//
//    fun renameStream(gradeName: String, oldName: String, newName: String): Boolean {
//        val grade = selectedGrade.value ?: return false
//        if (grade.streams.any { it.name.equals(newName, ignoreCase = true) }) return false
//
//        viewModelScope.launch {
//            try {
//                val response = repository.renameStream(gradeName, oldName, newName)
//                if (response.isSuccessful) {
//                    val index = grade.streams.indexOfFirst { it.name == oldName }
//                    if (index != -1) grade.streams[index] = Stream(newName)
//                    _snackbarMessage.value = "Stream renamed"
//                } else {
//                    _snackbarMessage.value = "Rename failed"
//                }
//            } catch (e: Exception) {
//                _snackbarMessage.value = "Error renaming stream: ${e.localizedMessage}"
//            }
//        }
//
//        return true
//    }
//    fun moveStreamUp(index: Int) {
//        val grade = selectedGrade.value ?: return
//        if (index > 0 && index < grade.streams.size) {
//            val temp = grade.streams[index]
//            grade.streams[index] = grade.streams[index - 1]
//            grade.streams[index - 1] = temp
//        }
//    }
//
//    fun moveStreamDown(index: Int) {
//        val grade = selectedGrade.value ?: return
//        if (index >= 0 && index < grade.streams.size - 1) {
//            val temp = grade.streams[index]
//            grade.streams[index] = grade.streams[index + 1]
//            grade.streams[index + 1] = temp
//        }
//    }
//
//}

