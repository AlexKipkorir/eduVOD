package com.example.eduvod.retrofit.response

data class SchoolResponse(
    val id: Int,
    val name: String,
    val moeRegNo: String,
    val kpsaRegNo: String,
    val curriculum: String,
    val category: String,
    val type: String,
    val composition: String,
    val mobile: String,
    val email: String,
    val region: String,
    val diocese: String,
    val county: String,
    val subCounty: String,
    val location: String,
    val address: String,
    val website: String,
    val hasAdmin: Boolean
)
