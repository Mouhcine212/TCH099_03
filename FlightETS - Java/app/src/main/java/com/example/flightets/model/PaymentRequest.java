package com.example.flightets.model;

import com.google.gson.annotations.SerializedName;

public class PaymentRequest {
    @SerializedName("id_reservation") private int idReservation;
    @SerializedName("montant") private double montant;
    @SerializedName("methode") private String methode;

    public PaymentRequest(int idReservation, double montant, String methode) {
        this.idReservation = idReservation;
        this.montant = montant;
        this.methode = methode;
    }
}
