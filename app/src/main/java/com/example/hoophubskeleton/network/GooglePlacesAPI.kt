package com.example.hoophubskeleton.network

import com.example.hoophubskeleton.data.PlaceApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesAPI {
    @GET("nearbysearch/json")
    fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("key") apiKey: String
    ): Call<PlaceApiResponse>
}
