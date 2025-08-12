package com.example.flightets.model;

import com.google.gson.annotations.SerializedName;

public class CancelResponse {
    @SerializedName("success") public boolean success;
    @SerializedName("error")   public String  error;
    @SerializedName("statut_reservation") public String statut_reservation;
    @SerializedName("statut_paiement")    public String statut_paiement;
}
