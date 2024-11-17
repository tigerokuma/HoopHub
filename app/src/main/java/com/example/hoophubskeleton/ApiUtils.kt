package com.example.hoophubskeleton

import com.google.android.gms.maps.model.LatLng
import com.example.hoophubskeleton.data.BasketballCourt
import com.example.hoophubskeleton.data.PlaceApiResponse
import com.example.hoophubskeleton.network.GooglePlacesAPI
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

fun fetchNearbyBasketballCourts(
    userLocation: LatLng,
    radius: Int,
    apiKey: String,
    onSuccess: (List<BasketballCourt>) -> Unit,
    onFailure: (Throwable) -> Unit
) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/place/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(GooglePlacesAPI::class.java)

    val location = "${userLocation.latitude},${userLocation.longitude}"
    service.getNearbyPlaces(location, radius, "basketball_court", apiKey).enqueue(object : Callback<PlaceApiResponse> {
        override fun onResponse(call: Call<PlaceApiResponse>, response: Response<PlaceApiResponse>) {
            if (response.isSuccessful) {
                val results = response.body()?.results
                val courts = results?.mapNotNull { result ->
                    val name = result.name
                    val address = result.vicinity
                    val location = result.geometry?.location
                    if (name != null && address != null && location != null) {
                        BasketballCourt(
                            name = name,
                            address = address,
                            rating = result.rating ?: 0.0f,
                            latitude = location.lat,
                            longitude = location.lng
                        )
                    } else null
                } ?: emptyList()
                onSuccess(courts)
            } else {
                onFailure(Throwable("Failed to fetch courts: ${response.errorBody()?.string()}"))
            }
        }

        override fun onFailure(call: Call<PlaceApiResponse>, t: Throwable) {
            onFailure(t)
        }
    })
}
