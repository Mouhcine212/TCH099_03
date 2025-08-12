package com.example.flightets.model;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    private String email;

    @SerializedName("motDePasse")
    private String motDePasse;

    @SerializedName("prenom")
    private String prenom;

    @SerializedName("nom")
    private String nom;

    // Optional; send digits only like the web JS does
    @SerializedName("telephone")
    private String telephone;

    public RegisterRequest(String email, String motDePasse, String prenom, String nom, String telephone) {
        this.email = email;
        this.motDePasse = motDePasse;
        this.prenom = prenom;
        this.nom = nom;
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
