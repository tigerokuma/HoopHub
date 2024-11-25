package com.example.hoophubskeleton

import com.google.android.gms.maps.model.LatLng
import com.example.hoophubskeleton.data.BasketballCourt
import com.example.hoophubskeleton.data.PlaceApiResponse
import com.example.hoophubskeleton.network.GooglePlacesAPI
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


import android.content.Context
// import org.chromium.base.Callback

fun fetchNearbyBasketballCourts(
    context: Context,
    userLocation: LatLng,
    radius: Int,
    onSuccess: (List<BasketballCourt>) -> Unit,
    onFailure: (Throwable) -> Unit
) {
    val apiKey = context.getString(R.string.google_maps_key) // Fetch API key from res/values/strings.xml

    val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/place/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(GooglePlacesAPI::class.java)
    val location = "${userLocation.latitude},${userLocation.longitude}"

    service.getNearbyPlaces(location, radius, null, "basketball court", apiKey)
        .enqueue(object : Callback<PlaceApiResponse> {
            override fun onResponse(
                call: Call<PlaceApiResponse>,
                response: Response<PlaceApiResponse>
            ) {
                if (response.isSuccessful) {
                    val courts = response.body()?.results
                        ?.mapNotNull { result ->
                            val location = result.geometry?.location
                            if (result.name != null && result.vicinity != null && location != null) {
                                BasketballCourt(
                                    name = result.name,
                                    address = result.vicinity,
                                    rating = result.rating ?: 0.0f,
                                    latitude = location.lat,
                                    longitude = location.lng
                                )
                            } else null
                        } ?: emptyList()

                    onSuccess(courts)
                } else {
                    val errorBody = response.errorBody()?.string()
                    onFailure(Throwable("Failed to fetch courts: $errorBody"))
                }
            }

            override fun onFailure(call: Call<PlaceApiResponse>, t: Throwable) {
                onFailure(t)
            }
        })
}

