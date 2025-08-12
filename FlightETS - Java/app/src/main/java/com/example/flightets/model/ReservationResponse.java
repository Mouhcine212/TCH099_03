package com.example.flightets.model;

import com.google.gson.annotations.SerializedName;

public class ReservationResponse {
    @SerializedName("success") private boolean success;
    @SerializedName("id_reservation") private int idReservation;
    @SerializedName("error") private String error;

    public boolean isSuccess() { return success; }
    public int getIdReservation() { return idReservation; }
    public String getError() { return error; }
}
