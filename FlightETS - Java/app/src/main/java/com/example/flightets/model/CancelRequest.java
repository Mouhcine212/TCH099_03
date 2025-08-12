package com.example.flightets.model;

import com.google.gson.annotations.SerializedName;

public class CancelRequest {
    @SerializedName("id_reservation") int id_reservation;
    public CancelRequest(int id) { this.id_reservation = id; }
}
