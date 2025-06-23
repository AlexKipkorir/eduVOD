package com.example.eduvod.model

data class Grade(
    val name: String,
    val curriculum: String,
    val hasSchool: Boolean = false,
    val streams: MutableList<Stream>
)
