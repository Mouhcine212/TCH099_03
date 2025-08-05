package com.example.flightets;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/search_flights")
    Call<List<Flight>> searchFlights(@Body SearchRequest request);
}

