package com.example.eduvod.model


data class School(
    val id: Int,
    val name: String,
    val moeRegNo: String,
    val kpsaRegNo: String? = null,
    val curriculum: String? = null,
    val category: String,
    val type: String,
    val composition: String,
    val mobile: String? = null,
    val email: String,
    val region: String,
    val diocese: String? = null,
    val county: String,
    val subCounty: String,
    val location: String? = null,
    val address: String? = null,
    val website: String? = null,
    val hasAdmin: Boolean = false
)


