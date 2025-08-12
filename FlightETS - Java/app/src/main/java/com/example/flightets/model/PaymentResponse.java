package com.example.flightets.model;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse {
    @SerializedName("success") private boolean success;
    @SerializedName("id_reservation") private Integer idReservation;
    @SerializedName("error") private String error;

    public boolean isSuccess() { return success; }
    public Integer getIdReservation() { return idReservation; }
    public String getError() { return error; }
}
