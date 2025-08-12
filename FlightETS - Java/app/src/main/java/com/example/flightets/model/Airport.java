package com.example.flightets.model;

import com.google.gson.annotations.SerializedName;

public class Airport {
    @SerializedName("VILLE")
    private String ville;

    @SerializedName("CODE_IATA")
    private String codeIata;

    public String getVille() { return ville; }
    public String getCodeIata() { return codeIata; }
}
