package com.example.hoophubskeleton.data

data class BasketballCourt(
    val name: String,
    val address: String,
    val rating: Float,
    val latitude: Double,
    val longitude: Double,
    var distance: Float = 0f // Distance in km
)
