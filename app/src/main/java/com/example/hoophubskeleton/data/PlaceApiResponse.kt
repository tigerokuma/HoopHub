package com.example.hoophubskeleton.data

data class PlaceApiResponse(
    val results: List<PlaceResult>,
    val status: String
)

data class PlaceResult(
    val name: String?,
    val vicinity: String?,
    val rating: Float?,
    val geometry: Geometry?
)

data class Geometry(
    val location: Location?
)

data class Location(
    val lat: Double,
    val lng: Double
)
