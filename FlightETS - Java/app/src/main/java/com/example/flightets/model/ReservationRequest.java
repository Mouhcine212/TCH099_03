package com.example.flightets.model;

import com.google.gson.annotations.SerializedName;

public class ReservationRequest {
    @SerializedName("id_vol") private int idVol;
    @SerializedName("numero_siege") private String numeroSiege;

    public ReservationRequest(int idVol, String numeroSiege) {
        this.idVol = idVol;
        this.numeroSiege = numeroSiege;
    }
}
