package com.example.eduvod.retrofit.response

import com.example.eduvod.model.School

data class SchoolResponse(
    val id: Int,
    val name: String?,
    val moeRegNo: String?,
    val kpsaRegNo: String?,
    val curriculum: String?,
    val category: String?,
    val type: String?,
    val composition: String?,
    val mobile: String?,
    val email: String?,
    val region: String?,
    val diocese: String?,
    val county: String?,
    val subCounty: String?,
    val location: String?,
    val address: String?,
    val website: String?,
    val hasAdmin: Boolean
)
fun SchoolResponse.toSchool(): School {
    return School(
        id = this.id,
        name = this.name ?: "",
        moeRegNo = this.moeRegNo ?: "",
        kpsaRegNo = this.kpsaRegNo,
        curriculum = this.curriculum,
        category = this.category ?: "",
        type = this.type ?: "",
        composition = this.composition ?: "",
        mobile = this.mobile,
        email = this.email ?: "",
        region = this.region ?: "",
        diocese = this.diocese,
        county = this.county ?: "",
        subCounty = this.subCounty ?: "",
        location = this.location,
        address = this.address,
        website = this.website,
        hasAdmin = false
    )
}
