package com.example.eduvod.model

data class Stream(
    val name: String
)

data class RenameStreamRequest(
    val oldName: String,
    val newName: String
)

