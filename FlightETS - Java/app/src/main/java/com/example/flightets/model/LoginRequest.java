package com.example.flightets.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    private String email;

    // Keep exact JSON key used by backend: "motDePasse"
    @SerializedName("motDePasse")
    private String motDePasse;

    public LoginRequest(String email, String motDePasse) {
        this.email = email;
        this.motDePasse = motDePasse;
    }

    public String getEmail() { return email; }
    public String getMotDePasse() { return motDePasse; }
    public void setEmail(String email) { this.email = email; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}
