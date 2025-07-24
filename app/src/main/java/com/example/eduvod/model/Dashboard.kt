package com.example.eduvod.model

data class SuperAdminDashboardResponse(
    val studentCountByGender: Map<String, Int>,
    val differentlyAbledByGender: Map<String, Int>,
    val teacherCountByGender: Map<String, Int>,
    val guardianCount: Int,
    val studentsPerClass: Map<String, Int>,
    val studentsPerStream: Map<String, Int>,
    val schoolsPerRegion: Map<String, Int>
)

