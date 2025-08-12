package com.example.flightets.model;

import com.google.gson.annotations.SerializedName;

public class SearchRequest {

    @SerializedName("origine")
    private String origine;

    @SerializedName("destination")
    private String destination;

    // Optional (omit when null)
    @SerializedName("dateDepart")
    private String dateDepart;

    public SearchRequest(String origine, String destination, String dateDepart) {
        this.origine = origine;
        this.destination = destination;
        this.dateDepart = dateDepart; // pass null to omit
    }

    // Convenience ctor when you don’t have a date
    public SearchRequest(String origine, String destination) {
        this(origine, destination, null);
    }

    public String getOrigine() { return origine; }
    public String getDestination() { return destination; }
    public String getDateDepart() { return dateDepart; }

    public void setOrigine(String origine) { this.origine = origine; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setDateDepart(String dateDepart) { this.dateDepart = dateDepart; }
}
