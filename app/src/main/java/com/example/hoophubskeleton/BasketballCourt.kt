package com.example.hoophubskeleton

data class BasketballCourt(
    val name: String,
    val address: String,
    val rating: Double,
    val latitude: Double,
    val longitude: Double,
    var distance: Float = 0f // Distance in km
)
