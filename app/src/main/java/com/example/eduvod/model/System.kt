package com.example.eduvod.model

data class SimpleNameRequest(val name: String)

data class SimpleItem(
    val id: Int,
    val name: String
)
data class RegionResponse(val id: Long, val name: String)

data class CountyResponse(
    val id: Long,
    val name: String,
    val regionId: Long,
    val regionName: String
)

data class CountyRequest(val name: String, val regionId: Long)

data class SubCountyResponse(
    val id: Long,
    val name: String,
    val countyId: Long,
    val countyName: String,
    val regionId: Long,
    val regionName: String
)

data class SubCountyRequest(val name: String, val countyId: Long)

