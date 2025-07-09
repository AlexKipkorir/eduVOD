package com.example.eduvod.model

data class Grade(
    val id: Int,
    val name: String,
    val curriculum: String
)

data class GradeRequest(
    val name: String,
    val curriculumId: Int
)

data class Curriculum(
    val id: Int,
    val name: String
)

