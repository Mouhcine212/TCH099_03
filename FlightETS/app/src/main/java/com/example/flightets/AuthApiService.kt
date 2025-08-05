package com.example.flightets

import com.example.flightets.LoginRequest
import com.example.flightets.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApiService {
    @Headers("Content-Type: application/json")
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/search_flights")
    fun searchFlights(@Body request: SearchRequest): Call<List<Flight>>

}
