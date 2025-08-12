package com.example.flightets.model;

import com.google.gson.annotations.SerializedName;

public class UpdateUserRequest {
    @SerializedName("nom") public String nom;
    @SerializedName("email") public String email;
    @SerializedName("telephone") public String telephone;

    public UpdateUserRequest(String nom, String email, String telephone) {
        this.nom = nom;
        this.email = email;
        this.telephone = telephone;
    }
}
