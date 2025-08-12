package com.example.flightets;

import com.example.flightets.model.Airport;
import com.example.flightets.model.CancelRequest;
import com.example.flightets.model.CancelResponse;
import com.example.flightets.model.ChangePasswordRequest;
import com.example.flightets.model.ChangePasswordResponse;
import com.example.flightets.model.Flight;
import com.example.flightets.model.LoginRequest;
import com.example.flightets.model.LoginResponse;
import com.example.flightets.model.PaymentRequest;
import com.example.flightets.model.PaymentResponse;
import com.example.flightets.model.RegisterRequest;
import com.example.flightets.model.RegisterResponse;
import com.example.flightets.model.ReservationItem;
import com.example.flightets.model.ReservationRequest;
import com.example.flightets.model.ReservationResponse;
import com.example.flightets.model.SearchRequest;
import com.example.flightets.model.UpdateUserRequest;
import com.example.flightets.model.UpdateUserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthApiService {

    // Registration / Login
    @Headers("Content-Type: application/json")
    @POST("api/endpoints/user_post.php")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @Headers("Content-Type: application/json")
    @POST("api/endpoints/login.php")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/endpoints/get_airports.php")
    Call<List<Airport>> getAirports(@Header("Authorization") String bearer);

    @Headers("Content-Type: application/json")
    @POST("api/endpoints/search_flights.php")
    Call<List<Flight>> searchFlights(@Header("Authorization") String bearer, @Body SearchRequest request);

    @Headers("Content-Type: application/json")
    @POST("api/endpoints/create_reservation.php")
    Call<ReservationResponse> createReservation(@Header("Authorization") String bearer, @Body ReservationRequest request);

    @Headers("Content-Type: application/json")
    @POST("api/endpoints/process_payment.php")
    Call<PaymentResponse> processPayment(@Header("Authorization") String bearer, @Body PaymentRequest request);

    @GET("api/endpoints/historique.php")
    Call<List<ReservationItem>> getReservations(@Header("Authorization") String bearer);

    @POST("api/endpoints/annuler_reservation.php")
    Call<CancelResponse> cancelReservation(@Header("Authorization") String bearer,
                                           @Body CancelRequest request);

    @POST("api/endpoints/update_user.php")
    Call<UpdateUserResponse> updateUser(@Header("Authorization") String bearer,
                                        @Body UpdateUserRequest request);

    @POST("api/endpoints/change_password.php")
    Call<ChangePasswordResponse> changePassword(@Header("Authorization") String bearer,
                                                @Body ChangePasswordRequest request);


}
